package taskmanager;

import exception.ManagerSaveException;
import task.Task;
import task.Epic;
import task.Subtask;
import taskstatus.TaskStatus;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    public final File file;
    private final HistoryManager historyManager;

    public FileBackedTaskManager(File file) {
        super(); // Вызов конструктора родительского класса
        this.file = file;
        this.historyManager = Managers.getDefaultHistory(); // Используем метод из Managers для инициализации
    }

    // Метод для автосохранения в файл
    public void save() {
        try (FileWriter writer = new FileWriter(file)) {
            // Заголовок CSV
            writer.write("id,type,name,status,description,epic\n");

            // Сохранение всех задач
            for (Task task : getAllTasks()) {
                writer.write(taskToString(task) + "\n");
            }

            // Сохранение всех эпиков и их подзадач
            for (Epic epic : getAllEpics()) {
                writer.write(taskToString(epic) + "\n");
                for (Subtask subtask : getSubtasksByEpic(epic.getId())) {
                    writer.write(taskToString(subtask) + "\n");
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл", e);
        }
    }

    // Преобразование задачи в строку CSV
    private String taskToString(Task task) {
        String epicId = (task instanceof Subtask) ? String.valueOf(((Subtask) task).getEpicId()) : "";
        return String.format("%d,%s,%s,%s,%s,%s",
                task.getId(),
                task.getType().name(),
                task.getName(),
                task.getStatus().name(),
                task.getDescription(),
                epicId);
    }

    // Преобразование строки CSV в задачу
    private static Task fromString(String value) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        String type = fields[1];
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];

        switch (type) {
            case "TASK":
                return new Task(id, name, description, status);
            case "EPIC":
                return new Epic(id, name, description, status);
            case "SUBTASK":
                int epicId = Integer.parseInt(fields[5]);
                return new Subtask(id, name, description, status, epicId);
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }

    // Статический метод для загрузки менеджера из файла
    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            // Проверяем, есть ли строки для обработки
            if (lines.size() > 1) { // Первую строку игнорируем (это заголовок)
                for (String line : lines.subList(1, lines.size())) {
                    Task task = fromString(line);
                    if (task instanceof Epic) {
                        manager.epics.put(task.getId(), (Epic) task);
                    } else if (task instanceof Subtask) {
                        manager.subtasks.put(task.getId(), (Subtask) task);
                    } else {
                        manager.tasks.put(task.getId(), task);
                    }
                    // Добавляем задачу в историю
                    manager.historyManager.add(task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке из файла", e); // Используем новое исключение
        }
        return manager;
    }

    // Переопределение методов с добавлением вызова save()
    @Override
    public Task createTask(String name, String description, TaskStatus status) {
        Task task = super.createTask(name, description, status);
        historyManager.add(task); // Добавляем задачу в историю
        save();
        return task;
    }

    @Override
    public Subtask createSubtask(String name, String description, TaskStatus status, int epicId) {
        Subtask subtask = super.createSubtask(name, description, status, epicId);
        historyManager.add(subtask); // Добавляем подзадачу в историю
        save();
        return subtask;
    }

    @Override
    public Epic createEpic(String name, String description, TaskStatus status) {
        Epic epic = super.createEpic(name, description, status);
        historyManager.add(epic); // Добавляем эпик в историю
        save();
        return epic;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        historyManager.add(task); // Обновляем задачу в истории
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        try {
            super.updateSubtask(subtask); // Попытка обновить подзадачу
            historyManager.add(subtask); // Обновляем подзадачу в истории
            save();
        } catch (TaskNotFoundException e) {
            System.out.println("Ошибка: подзадача не найдена.");
            e.printStackTrace();
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        historyManager.add(epic); // Обновляем эпик в истории
        save();
    }

    @Override
    public void deleteTask(int id) {
        try {
            super.deleteTask(id);  // Вызов родительского метода
            historyManager.remove(id); // Удаляем задачу из истории
            save(); // Сохраняем изменения
        } catch (TaskNotFoundException e) {
            // Обработка исключения: можно залогировать или игнорировать
            System.out.println("Задача не найдена: " + e.getMessage());
        }
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        historyManager.remove(id); // Удаляем подзадачу из истории
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        historyManager.remove(id); // Удаляем эпик из истории
        save();
    }

    // Метод для получения истории
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}