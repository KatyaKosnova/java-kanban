import task.Task;
import taskmanager.FileBackedTaskManager;
import taskstatus.TaskStatus;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.logging.Level;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        File file = new File("taskManager.csv"); // Используем фиксированное имя файла
        FileBackedTaskManager manager;

        try {
            manager = FileBackedTaskManager.loadFromFile(file);
        } catch (Exception e) {
            // Если файл не найден, создаем новый экземпляр менеджера
            manager = new FileBackedTaskManager(file);
            System.out.println("Файл не найден. Создан новый менеджер задач.");
        }

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("Выберите действие:");
                System.out.println("1. Создать задачу");
                System.out.println("2. Создать эпик");
                System.out.println("3. Создать подзадачу");
                System.out.println("4. Сохранить задачи в файл");
                System.out.println("5. Загрузить задачи из файла");
                System.out.println("6. Показать историю");
                System.out.println("0. Выход");

                int choice;
                try {
                    choice = scanner.nextInt();
                    scanner.nextLine(); // Очистка буфера
                } catch (InputMismatchException e) {
                    System.out.println("Пожалуйста, введите корректный номер действия.");
                    scanner.nextLine(); // Очистка буфера
                    continue;
                }

                switch (choice) {
                    case 1:
                        // Создание задачи
                        System.out.println("Создание задачи...");
                        break;
                    case 2:
                        // Создание эпика
                        System.out.println("Создание эпика...");
                        break;
                    case 3:
                        // Создание подзадачи
                        System.out.print("Введите имя подзадачи: ");
                        String subtaskName = scanner.nextLine();
                        System.out.print("Введите описание подзадачи: ");
                        String subtaskDesc = scanner.nextLine();
                        System.out.print("Введите ID эпика: ");
                        int epicId;

                        try {
                            epicId = scanner.nextInt();
                            scanner.nextLine(); // Очистка буфера
                        } catch (InputMismatchException e) {
                            System.out.println("Пожалуйста, введите корректный ID эпика.");
                            scanner.nextLine(); // Очистка буфера
                            continue; // Пропускаем итерацию
                        }

                        // Ввод продолжительности
                        System.out.print("Введите продолжительность (в часах): ");
                        long durationHours = scanner.nextLong();
                        scanner.nextLine(); // Очистка буфера
                        Duration duration = Duration.ofHours(durationHours);

                        // Ввод времени начала
                        System.out.print("Введите время начала (в формате ГГГГ-ММ-ДД ЧЧ:ММ): ");
                        String startTimeInput = scanner.nextLine();
                        LocalDateTime startTime;
                        try {
                            startTime = LocalDateTime.parse(startTimeInput);
                        } catch (DateTimeParseException e) {
                            System.out.println("Неверный формат времени. Пожалуйста, используйте формат ГГГГ-ММ-ДД ЧЧ:ММ.");
                            continue; // Пропускаем итерацию
                        }

                        // Добавление подзадачи
                        manager.createSubtask(subtaskName, subtaskDesc, TaskStatus.NEW, epicId, duration, startTime);
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
                        return; // Выход из программы
                    default:
                        System.out.println("Неверный выбор.");
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Произошла ошибка", e);
        }
    }
}