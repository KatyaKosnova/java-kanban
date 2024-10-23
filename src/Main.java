import server.HttpTaskServer;
import task.Task;
import taskmanager.FileBackedTaskManager;
import taskstatus.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        // Создаем экземпляр вашего менеджера задач
        File file = new File("taskManager.csv");
        FileBackedTaskManager manager;

        try {
            manager = FileBackedTaskManager.loadFromFile(file);
        } catch (Exception e) {
            manager = new FileBackedTaskManager(file);
            System.out.println("Файл не найден. Создан новый менеджер задач.");
            logger.log(Level.WARNING, "Файл не найден, создан новый менеджер задач.", e);
        }

        // Создаем экземпляр вашего сервера
        HttpTaskServer server;
        try {
            server = new HttpTaskServer(manager); // Передаем менеджер задач
            server.start(); // Запускаем сервер
            System.out.println("Сервер запущен на порту: 8085");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка при запуске сервера", e);
            return; // Завершаем программу, если сервер не удалось запустить
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
                        System.out.println("Создание задачи...");
                        // Реализация создания задачи
                        break;
                    case 2:
                        System.out.println("Создание эпика...");
                        // Реализация создания эпика
                        break;
                    case 3:
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
                            continue;
                        }

                        // Ввод продолжительности
                        System.out.print("Введите продолжительность (в часах): ");
                        long durationHours;
                        try {
                            durationHours = scanner.nextLong();
                            if (durationHours < 0) {
                                throw new IllegalArgumentException("Продолжительность не может быть отрицательной.");
                            }
                            scanner.nextLine(); // Очистка буфера
                        } catch (InputMismatchException | IllegalArgumentException e) {
                            System.out.println("Пожалуйста, введите корректную продолжительность.");
                            scanner.nextLine(); // Очистка буфера
                            continue;
                        }

                        Duration duration = Duration.ofHours(durationHours);

                        // Ввод времени начала
                        System.out.print("Введите время начала (в формате ГГГГ-ММ-ДД ЧЧ:ММ): ");
                        String startTimeInput = scanner.nextLine();
                        LocalDateTime startTime;
                        try {
                            startTime = LocalDateTime.parse(startTimeInput);
                        } catch (DateTimeParseException e) {
                            System.out.println("Неверный формат времени. Пожалуйста, используйте формат ГГГГ-ММ-ДД ЧЧ:ММ.");
                            continue;
                        }

                        // Добавление подзадачи
                        manager.createSubtask(subtaskName, subtaskDesc, TaskStatus.NEW, epicId, duration, startTime);
                        System.out.println("Подзадача создана.");
                        break;
                    case 4:
                        try {
                            manager.save();
                            System.out.println("Задачи сохранены в файл.");
                        } catch (Exception e) {
                            System.out.println("Ошибка при сохранении задач в файл: " + e.getMessage());
                            logger.log(Level.SEVERE, "Ошибка при сохранении задач в файл.", e);
                        }
                        break;
                    case 5:
                        try {
                            manager = FileBackedTaskManager.loadFromFile(file);
                            System.out.println("Задачи загружены из файла.");
                        } catch (Exception e) {
                            System.out.println("Ошибка при загрузке задач из файла: " + e.getMessage());
                            logger.log(Level.SEVERE, "Ошибка при загрузке задач из файла.", e);
                        }
                        break;
                    case 6:
                        List<Task> history = manager.getHistory();
                        System.out.println("История задач:");
                        for (Task task : history) {
                            System.out.println(task.getId() + " " + task.getName() + " - " + task.getStatus());
                        }
                        break;
                    case 0:
                        System.out.println("Выход из программы...");
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
