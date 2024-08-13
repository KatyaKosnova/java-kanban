package TaskManager;
import Task.Task;
import Task.Epic;
import Task.Subtask;
import TaskStatus.TaskStatus;

import java.util.*;
import java.util.stream.Collectors;

public class TaskManager {
    private Map<Integer, Task> tasks;
    private Map<Integer, Subtask> subtasks;
    private Map<Integer, Epic> epics;
    private int currentId = 1;

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.epics = new HashMap<>();
    }

    public Task createTask(String name, String description, TaskStatus status) {
        Task task = new Task(currentId, name, description, status);
        tasks.put(currentId, task);
        currentId++;
        return task;
    }

    public Subtask createSubtask(String name, String description, TaskStatus status, int epicId) {
        Subtask subtask = new Subtask(currentId, name, description, status, epicId);
        subtasks.put(currentId, subtask);
        currentId++;

        // Get the epic and add the subtask to it
        Epic epic = epics.get(epicId);
        if (epic != null) {
            epic.addSubtask(subtask);
        } else {
            throw new IllegalArgumentException("Не удается создать подзадачу: epic с ID " + epicId + "не существует");
        }

        return subtask;
    }

    public Epic createEpic(String name, String description, TaskStatus status) {
        Epic epic = new Epic(currentId, name, description, status);
        epics.put(currentId, epic);
        currentId++;
        return epic;
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.updateSubtask(subtask);
        }
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
    }

    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtask(subtask);
    }

    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        for (Subtask subtask : epic.getSubtasks()) {
            subtasks.remove(subtask.getId());
        }
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public List<Subtask> getSubtasksByEpic(int epicId) {
        Epic epic = epics.get(epicId);
        return epic.getSubtasks();
    }

    private void updateEpicStatus(Epic epic) {
        List<Subtask> subtasks = epic.getSubtasks();
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