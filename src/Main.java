import task.Task;
import taskmanager.FileBackedTaskManager;
import taskstatus.TaskStatus;

import java.io.File;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            File file = File.createTempFile("taskManager", ".csv");
            FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(file);

            while (true) {
                System.out.println("Выберите действие:");
                System.out.println("1. Создать задачу");
                System.out.println("2. Создать эпик");
                System.out.println("3. Создать подзадачу");
                System.out.println("4. Сохранить задачи в файл");
                System.out.println("5. Загрузить задачи из файла");
                System.out.println("6. Показать историю");
                System.out.println("0. Выход");

                int choice = scanner.nextInt();
                scanner.nextLine(); // Очистка буфера

                switch (choice) {
                    case 1:
                        System.out.print("Введите имя задачи: ");
                        String taskName = scanner.nextLine();
                        System.out.print("Введите описание задачи: ");
                        String taskDesc = scanner.nextLine();
                        manager.createTask(taskName, taskDesc, TaskStatus.NEW);
                        System.out.println("Задача создана.");
                        break;
                    case 2:
                        System.out.print("Введите имя эпика: ");
                        String epicName = scanner.nextLine();
                        System.out.print("Введите описание эпика: ");
                        String epicDesc = scanner.nextLine();
                        manager.createEpic(epicName, epicDesc, TaskStatus.NEW);
                        System.out.println("Эпик создан.");
                        break;
                    case 3:
                        System.out.print("Введите имя подзадачи: ");
                        String subtaskName = scanner.nextLine();
                        System.out.print("Введите описание подзадачи: ");
                        String subtaskDesc = scanner.nextLine();
                        System.out.print("Введите ID эпика: ");
                        int epicId = scanner.nextInt();
                        scanner.nextLine(); // Очистка буфера
                        manager.createSubtask(subtaskName, subtaskDesc, TaskStatus.NEW, epicId);
                        System.out.println("Подзадача создана.");
                        break;
                    case 4:
                        manager.save();
                        System.out.println("Задачи сохранены в файл.");
                        break;
                    case 5:
                        manager = FileBackedTaskManager.loadFromFile(file);
                        System.out.println("Задачи загружены из файла.");
                        break;
                    case 6:
                        List<Task> history = manager.getHistory();
                        System.out.println("История задач:");
                        for (Task task : history) {
                            System.out.println(task.getId() + " " + task.getName() + " - " + task.getStatus());
                        }
                        break;
                    case 0:
                        return;
                    default:
                        System.out.println("Неверный выбор.");
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Произошла ошибка", e);
        }
    }
}