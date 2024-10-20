package taskmanager;

import exception.InvalidSubtaskException;
import task.Task;
import task.Epic;
import task.Subtask;
import taskstatus.TaskStatus;
import java.time.Duration;
import java.time.LocalDateTime;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Subtask> subtasks;
    protected final Map<Integer, Epic> epics;
    protected final HistoryManager historyManager;
    private int currentId = 1;

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
    }

    @Override
    public Task createTask(String name, String description, TaskStatus status) {
        Task task = new Task(currentId, name, description, status);
        tasks.put(currentId, task);
        currentId++;
        return task;
    }

    public Task addTask(Task task) {
        // Создаем новую задачу с текущим ID и передаем остальные параметры
        Task newTask = new Task(currentId, task.getName(), task.getDescription(), task.getStatus());
        tasks.put(currentId, newTask); // Сохраняем новую задачу в мапе
        currentId++; // Увеличиваем текущий ID для следующей задачи
        return newTask; // Возвращаем добавленную задачу
    }

    @Override
    public Subtask createSubtask(String name, String description, TaskStatus status, int epicId, Duration duration, LocalDateTime startTime) {
        int subtaskId = currentId++; // Генерация ID для новой подзадачи

        // Проверяем, существует ли эпик
        Epic epic = getEpic(epicId);
        if (epic == null) {
            throw new IllegalArgumentException("Эпик не найден.");
        }

        // Создаем подзадачу
        Subtask subtask = new Subtask(subtaskId, name, description, status, epicId, duration, startTime);

        // Добавляем подзадачу в эпик
        try {
            epic.addSubtask(subtask);
        } catch (InvalidSubtaskException e) {
            System.out.println("Ошибка добавления подзадачи: " + e.getMessage());
            return null; // Или выполните другое действие, если необходимо
        }

        // Сохраняем подзадачу в менеджере
        subtasks.put(subtaskId, subtask);

        return subtask; // Возвращаем созданную подзадачу
    }


    @Override
    public Epic createEpic(String name, String description, TaskStatus status) {
        Epic epic = new Epic(currentId, name, description, status);
        epics.put(currentId, epic);
        currentId++;
        return epic;
    }

    @Override
    public Task getTask(int id) throws TaskNotFoundException {
        Task task = tasks.get(id); // Предполагая, что tasks - это Map<Integer, Task>
        if (task == null) {
            throw new TaskNotFoundException("Task with id " + id + " not found."); // Если задача не найдена, выбрасываем исключение
        }
        return task; // Возвращаем найденную задачу
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            throw new IllegalArgumentException("Задача с ID " + task.getId() + " не найдена.");
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) throws TaskNotFoundException {
        if (!subtasks.containsKey(subtask.getId())) {
            throw new TaskNotFoundException("Подзадача с ID " + subtask.getId() + " не найдена.");
        }
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.updateSubtask(subtask);
            updateEpicStatus(epic);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null || !epics.containsKey(epic.getId())) {
            throw new IllegalArgumentException("Невозможно обновить эпик. Эпик не найден.");
        }
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
    }

    @Override
    public void deleteTask(int id) throws TaskNotFoundException {
        Task task = getTask(id); // Предполагаем, что метод getTask выбрасывает TaskNotFoundException
        // Код для удаления задачи
        tasks.remove(id);
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(subtask);
                updateEpicStatus(epic);
            }
        }
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Subtask subtask : epic.getSubtasks()) {
                subtasks.remove(subtask.getId());
            }
        }
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasksByEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            return epic.getSubtasks();
        }
        return Collections.emptyList();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void updateEpicStatus(Epic epic) {
        List<Subtask> subtasks = epic.getSubtasks();
        if (subtasks.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        Set<TaskStatus> uniqueStatuses = subtasks.stream()
                .map(Subtask::getStatus)
                .collect(Collectors.toSet());

        if (uniqueStatuses.size() == 1) {
            epic.setStatus(subtasks.get(0).getStatus());
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public void clear() {
        tasks.clear();
        subtasks.clear();
        epics.clear();
        historyManager.clear(); // Очищаем историю посещений задач
        currentId = 1;  // Сбрасываем идентификатор
    }
}
