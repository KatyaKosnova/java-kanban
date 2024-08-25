package TaskManager;

import Task.Task;
import Task.Epic;
import Task.Subtask;
import TaskStatus.TaskStatus;

import java.util.List;

public interface TaskManager {

    Task createTask(String name, String description, TaskStatus status);

    Subtask createSubtask(String name, String description, TaskStatus status, int epicId);

    Epic createEpic(String name, String description, TaskStatus status);

    Task getTask(int id);

    Subtask getSubtask(int id);

    Epic getEpic(int id);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    void deleteTask(int id);

    void deleteSubtask(int id);

    void deleteEpic(int id);

    List<Task> getAllTasks();

    List<Subtask> getAllSubtasks();

    List<Epic> getAllEpics();

    List<Subtask> getSubtasksByEpic(int epicId);

    List<Task> getHistory();
}
