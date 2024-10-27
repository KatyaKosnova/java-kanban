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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;
    private final HistoryManager historyManager;

    public FileBackedTaskManager(File file) {
        super();
        this.file = file;
        this.historyManager = Managers.getDefaultHistory();
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task); // Вызов родительского метода
        save(); // Сохраняем изменения
    }

    // Метод для автосохранения в файл
    public void save() {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic,duration,startTime\n");
            for (Task task : getAllTasks()) {
                writer.write(taskToString(task) + "\n");
            }
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
        String duration = (task.getDuration() != null) ? task.getDuration().toString() : "";
        String startTime = (task.getStartTime() != null) ? task.getStartTime().toString() : "";
        return String.format("%d,%s,%s,%s,%s,%s,%s,%s",
                task.getId(),
                task.getType().name(),
                task.getName(),
                task.getStatus().name(),
                task.getDescription(),
                epicId,
                duration,
                startTime);
    }

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
                if (fields.length < 8) { // Проверяем, достаточно ли полей для подзадачи
                    throw new IllegalArgumentException("Недостаточно данных для подзадачи: " + value);
                }
                int epicId = Integer.parseInt(fields[5]);

                Duration duration;
                LocalDateTime startTime;

                try {
                    duration = Duration.parse(fields[6]); // Парсинг продолжительности
                } catch (Exception e) {
                    throw new IllegalArgumentException("Неверный формат продолжительности для подзадачи: " + fields[6], e);
                }

                try {
                    startTime = LocalDateTime.parse(fields[7]); // Парсинг времени начала
                } catch (Exception e) {
                    throw new IllegalArgumentException("Неверный формат времени начала для подзадачи: " + fields[7], e);
                }

                return new Subtask(id, name, description, status, epicId, duration, startTime);
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            if (lines.size() > 1) {
                for (String line : lines.subList(1, lines.size())) {
                    Task task = fromString(line);
                    if (task instanceof Epic) {
                        manager.epics.put(task.getId(), (Epic) task);
                    } else if (task instanceof Subtask) {
                        manager.subtasks.put(task.getId(), (Subtask) task);
                    } else {
                        manager.tasks.put(task.getId(), task);
                    }
                    manager.historyManager.add(task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке из файла", e);
        }
        return manager;
    }

    // Переопределение методов с добавлением вызова save()
    @Override
    public Task createTask(String name, String description, TaskStatus status) {
        Task task = super.createTask(name, description, status);
        historyManager.add(task);
        save();
        return task;
    }

    @Override
    public Subtask createSubtask(String name, String description, TaskStatus status, int epicId, Duration duration, LocalDateTime startTime) {
        Subtask subtask = super.createSubtask(name, description, status, epicId, duration, startTime);
        if (subtask != null) {
            historyManager.add(subtask); // Добавляем подзадачу в историю
            save(); // Сохраняем изменения
        }
        return subtask;
    }

    @Override
    public Epic createEpic(String name, String description, TaskStatus status) {
        Epic epic = super.createEpic(name, description, status);
        historyManager.add(epic);
        save();
        return epic;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        historyManager.add(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) throws TaskNotFoundException {
        super.updateSubtask(subtask);
        historyManager.add(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        historyManager.add(epic);
        save();
    }

    @Override
    public void deleteTask(int id) {
        try {
            super.deleteTask(id);  // Вызов родительского метода
            historyManager.remove(id); // Удаляем задачу из истории
            save(); // Сохраняем изменения
        } catch (TaskNotFoundException e) {
            System.out.println("Задача не найдена: " + e.getMessage());
        }
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        historyManager.remove(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        historyManager.remove(id);
        save();
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
