import task.Task;
import taskstatus.TaskStatus;
import taskmanager.Managers;
import taskmanager.TaskManager;
import task.Epic;
import task.Subtask;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TaskManager taskManager = Managers.getDefault();

        while (true) {
            System.out.println("Выберите действие:");
            System.out.println("1. Создать задачу");
            System.out.println("2. Создать подзадачу");
            System.out.println("3. Создать эпик");
            System.out.println("4. Показать все задачи");
            System.out.println("5. Показать историю");
            System.out.println("6. Выход");

            int action = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (action) {
                case 1:
                    System.out.println("Введите название задачи:");
                    String taskName = scanner.nextLine();
                    System.out.println("Введите описание задачи:");
                    String taskDescription = scanner.nextLine();
                    System.out.println("Введите статус задачи (NEW, IN_PROGRESS, DONE):");
                    String taskStatusStr = scanner.nextLine();
                    TaskStatus taskStatus = TaskStatus.valueOf(taskStatusStr);
                    Task task = taskManager.createTask(taskName, taskDescription, taskStatus);
                    System.out.println("Задача создана: " + task);
                    break;

                case 2:
                    System.out.println("Введите название подзадачи:");
                    String subtaskName = scanner.nextLine();
                    System.out.println("Введите описание подзадачи:");
                    String subtaskDescription = scanner.nextLine();
                    System.out.println("Введите статус подзадачи (NEW, IN_PROGRESS, DONE):");
                    String subtaskStatusStr = scanner.nextLine();
                    TaskStatus subtaskStatus = TaskStatus.valueOf(subtaskStatusStr);
                    System.out.println("Введите ID эпика:");
                    int epicId = scanner.nextInt();
                    scanner.nextLine();  // Consume newline
                    try {
                        Subtask subtask = taskManager.createSubtask(subtaskName, subtaskDescription, subtaskStatus, epicId);
                        System.out.println("Подзадача создана: " + subtask);
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    }
                    break;

                case 3:
                    System.out.println("Введите название эпика:");
                    String epicName = scanner.nextLine();
                    System.out.println("Введите описание эпика:");
                    String epicDescription = scanner.nextLine();
                    System.out.println("Введите статус эпика (NEW, IN_PROGRESS, DONE):");
                    String epicStatusStr = scanner.nextLine();
                    TaskStatus epicStatus = TaskStatus.valueOf(epicStatusStr);
                    Epic epic = taskManager.createEpic(epicName, epicDescription, epicStatus);
                    System.out.println("Эпик создан: " + epic);
                    break;

                case 4:
                    printAllTasks(taskManager);
                    break;

                case 5:
                    System.out.println("История:");
                    for (Task t : taskManager.getHistory()) {
                        System.out.println(t);
                    }
                    break;

                case 6:
                    System.out.println("Выход из программы.");
                    scanner.close();
                    return;

                default:
                    System.out.println("Неверный выбор. Попробуйте снова.");
                    break;
            }
        }
    }

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
    }
}