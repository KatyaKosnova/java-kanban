import Task.Epic;
import Task.Subtask;
import TaskStatus.TaskStatus;
import TaskManager.TaskManager;
import Task.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();


        Epic epic1 = taskManager.createEpic("Эпик 1", "", TaskStatus.NEW);
        Task task1 = taskManager.createTask("Задача 1", "", TaskStatus.NEW);
        Subtask subtask1 = taskManager.createSubtask("Подзадача 1", "", TaskStatus.NEW, task1.getId());
        Subtask subtask2 = taskManager.createSubtask("Подзадача 2", "", TaskStatus.NEW, task1.getId());
        Subtask subtask3 = taskManager.createSubtask("Подзадача 3", "", TaskStatus.NEW, epic1.getId());


        System.out.println("All tasks: " + taskManager.getAllTasks());
        System.out.println("All subtasks: " + taskManager.getAllSubtasks());
        System.out.println("All epics: " + taskManager.getAllEpics());


        task1.setStatus(TaskStatus.IN_PROGRESS);
        subtask1.setStatus(TaskStatus.DONE);
        taskManager.updateTask(task1);
        taskManager.updateSubtask(subtask1);


        System.out.println("Updated task status: " + taskManager.getTask(task1.getId()).getStatus());
        System.out.println("Updated subtask status: " + taskManager.getSubtask(subtask1.getId()).getStatus());
        System.out.println("Epic status: " + taskManager.getEpic(epic1.getId()).getStatus());


        taskManager.deleteTask(task1.getId());
        taskManager.deleteEpic(epic1.getId());


        System.out.println("Remaining tasks: " + taskManager.getAllTasks());
        System.out.println("Remaining subtasks: " + taskManager.getAllSubtasks());
        System.out.println("Remaining epics: " + taskManager.getAllEpics());
    }
}