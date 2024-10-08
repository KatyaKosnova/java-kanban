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
    private final TreeSet<Task> prioritizedTasks; // Хранение задач в порядке приоритета
    private int currentId = 1;
    private Epic epic;

    public InMemoryTaskManager() {
        this.epic = epic;
        this.tasks = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
        this.prioritizedTasks = new TreeSet<>(Comparator.comparing(Task:: getStartTime, Comparator.nullsLast(Comparator.naturalOrder())));
    }

    @Override
    public Task createTask(String name, String description, TaskStatus status) {
        Task task = new Task(currentId, name, description, status);
        tasks.put(currentId, task);
        currentId++;
        return task;
    }

    @Override
    public Subtask createSubtask(String name, String description, TaskStatus status, int epicId, Duration duration, LocalDateTime startTime) {
        Epic epic = epics.get(epicId); // Получаем эпик по ID
        if (epic == null) {
            throw new IllegalArgumentException("Эпик не найден.");
        }

        int subtaskId = subtasks.size() + 1; // Генерация ID для подзадачи

        // Создаем подзадачу
        Subtask subtask = new Subtask(subtaskId, name, description, status, epicId, duration, startTime);
        try {
            epic.addSubtask(subtask); // Добавляем подзадачу в эпик
        } catch (InvalidSubtaskException e) {
            System.out.println("Ошибка добавления подзадачи: " + e.getMessage());
            return null; // Или выполните другое действие, если необходимо
        }

        // Сохраняем подзадачу в менеджере
        subtasks.put(subtaskId, subtask); // Используем put для добавления подзадачи в Map

        // Добавляем подзадачу в приоритетный список, если у неё есть время начала
        if (startTime != null) {
            prioritizedTasks.add(subtask);
        }

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
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    @Override
    public void updateEpicStatus(int epicId) {
        Epic epic = getEpicById(epicId);
        List<Subtask> subtasks = epic.getSubtasks();

        if (subtasks.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        int newCount = 0;
        int inProgressCount = 0;
        int doneCount = 0;

        for (Subtask subtask : subtasks) {
            switch (subtask.getStatus()) {
                case NEW:
                    newCount++;
                    break;
                case IN_PROGRESS:
                    inProgressCount++;
                    break;
                case DONE:
                    doneCount++;
                    break;
            }
        }

        if (doneCount == subtasks.size()) {
            epic.setStatus(TaskStatus.DONE);
        } else if (newCount == subtasks.size()) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
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
            prioritizedTasks.remove(subtask); // Удаляем подзадачу из приоритетного списка
        }
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Subtask subtask : epic.getSubtasks()) {
                subtasks.remove(subtask.getId());
                prioritizedTasks.remove(subtask); // Удаляем подзадачу из приоритетного списка
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

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
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

    // Проверка пересечений по времени
    public boolean checkIntersection(Task task1, Task task2) {
        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = start1.plus(task1.getDuration());
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = start2.plus(task2.getDuration());

        return (start1.isBefore(end2) && end1.isAfter(start2));
    }

    @Override
    public void addTask(Task task) {
        // Валидация на пересечения
        for (Task existingTask : prioritizedTasks) {
            if (checkIntersection(existingTask, task)) {
                throw new IllegalArgumentException("Задача пересекается с существующей задачей: " + existingTask.getName());
            }
        }

        // Добавление задачи
        if (task instanceof Subtask) {
            subtasks.put(task.getId(), (Subtask) task);
            prioritizedTasks.add(task);
        } else {
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
        }
    }
}
