package taskmanager;

import task.Task;
import task.Epic;
import task.Subtask;
import taskstatus.TaskStatus;
import java.time.Duration;
import java.time.LocalDateTime;


import java.util.List;

public interface TaskManager {

    Task createTask(String name, String description, TaskStatus status);

    Subtask createSubtask(String name, String description, TaskStatus status, int epicId, Duration duration, LocalDateTime startTime);

    Epic createEpic(String name, String description, TaskStatus status);

    Task getTask(int id) throws TaskNotFoundException;

    Subtask getSubtask(int id);

    Epic getEpic(int id); // Существующий метод

    // Новый метод для получения эпика по ID
    Epic getEpicById(int id);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask) throws TaskNotFoundException;

    void updateEpic(Epic epic);

    void deleteTask(int id) throws TaskNotFoundException;

    void deleteSubtask(int id);

    void deleteEpic(int id);

    List<Task> getAllTasks();

    List<Subtask> getAllSubtasks();

    List<Epic> getAllEpics();

    List<Subtask> getSubtasksByEpic(int epicId);

    List<Task> getHistory();

    void clear();

    void addTask(Task task);
}