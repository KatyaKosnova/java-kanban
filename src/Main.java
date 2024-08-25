import Task.Epic;
import Task.Subtask;
import TaskStatus.TaskStatus;
import Task.Task;
import TaskManager.InMemoryTaskManager;
import TaskManager.Managers;
import TaskManager.TaskManager;
public class Main {

    public static void main(String[] args) {
        // Создаем менеджер задач через класс Managers
        TaskManager taskManager = Managers.getDefault();

        // Создаем несколько задач разного типа
        Task task1 = taskManager.createTask("Задача 1", "", TaskStatus.NEW);
        Task task2 = taskManager.createTask("Задача 2", "", TaskStatus.IN_PROGRESS);

        Epic epic1 = taskManager.createEpic("Эпик 1", "", TaskStatus.NEW);
        Subtask subtask1 = taskManager.createSubtask("Подзадача 1", "", TaskStatus.NEW, epic1.getId());

        Epic epic2 = taskManager.createEpic("Эпик 2", "", TaskStatus.NEW);
        Subtask subtask2 = taskManager.createSubtask("Подзадача 2", "", TaskStatus.IN_PROGRESS, epic2.getId());
        Subtask subtask3 = taskManager.createSubtask("Подзадача 3", "", TaskStatus.DONE, epic2.getId());

        // Вызов различных методов интерфейса TaskManager
        taskManager.getTask(task1.getId());
        taskManager.getEpic(epic1.getId());
        taskManager.getSubtask(subtask1.getId());
        taskManager.getEpic(epic2.getId());
        taskManager.getSubtask(subtask2.getId());
        taskManager.getTask(task2.getId());

        // Печать всех задач и истории просмотров
        printAllTasks(taskManager);
    }

    // Метод для печати всех задач, эпиков, подзадач и истории просмотров
    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("Эпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Subtask subtask : manager.getSubtasksByEpic(epic.getId())) {
                System.out.println("--> " + subtask);
            }
        }

        System.out.println("Подзадачи:");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}