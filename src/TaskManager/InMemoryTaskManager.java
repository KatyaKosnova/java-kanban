package taskmanager;

import task.Task;
import task.Epic;
import task.Subtask;
import taskstatus.TaskStatus;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    private final Map<Integer, Task> tasks;
    private final Map<Integer, Subtask> subtasks;
    private final Map<Integer, Epic> epics;
    private final HistoryManager historyManager;
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

    public Subtask createSubtask(String name, String description, TaskStatus status, int epicId) {
        // Получаем следующий уникальный ID для подзадачи
        int subtaskId = currentId++;

        // Проверяем, существует ли эпик
        Epic epic = getEpic(epicId);
        if (epic == null) {
            throw new IllegalArgumentException("Эпик не найден.");
        }

        // Создаем подзадачу
        Subtask subtask = new Subtask(subtaskId, name, description, status, epicId);

        // Добавляем подзадачу в эпик
        epic.addSubtask(subtask);

        // Сохраняем подзадачу в менеджере
        subtasks.put(subtaskId, subtask);

        return subtask;
    }

    @Override
    public Epic createEpic(String name, String description, TaskStatus status) {
        Epic epic = new Epic(currentId, name, description, status);
        epics.put(currentId, epic);
        currentId++;
        return epic;
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
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
    public void updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            throw new IllegalArgumentException("Подзадача с ID " + subtask.getId() + " не найдена.");
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
    public void deleteTask(int id) {
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
}
