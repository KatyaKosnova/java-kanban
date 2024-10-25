package test;

import org.junit.jupiter.api.AfterEach;
import task.Task;
import task.Epic;
import task.Subtask;
import taskmanager.*;
import taskstatus.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    // Метод для проверки пересечения двух задач
    protected boolean checkIntersection(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null) {
            return false; // Если у одной из задач нет времени начала, пересечение невозможно
        }
        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = start1.plus(task1.getDuration());
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = start2.plus(task2.getDuration());

        return (start1.isBefore(end2) && end1.isAfter(start2));
    }

    @BeforeEach
    void setUp() {
        taskManager = createTaskManager(); // метод создания конкретного менеджера
    }

    protected abstract T createTaskManager();

    @Test
    void addNewTask() {
        Task task = taskManager.createTask("Test addNewTask", "Test addNewTask description", TaskStatus.NEW);
        final int taskId = task.getId(); // Получаем id созданной задачи

        // Добавление задачи в менеджер
        taskManager.updateTask(task);

        try {
            // Получение задачи из менеджера
            final Task savedTask = taskManager.getTask(taskId);

            assertNotNull(savedTask, "Задача не найдена.");
            assertEquals(task, savedTask, "Задачи не совпадают.");

            // Получение всех задач
            final List<Task> tasks = taskManager.getAllTasks();

            assertNotNull(tasks, "Задачи не возвращаются.");
            assertEquals(1, tasks.size(), "Неверное количество задач.");
            assertEquals(task, tasks.get(0), "Задачи не совпадают.");
        } catch (TaskNotFoundException e) {
            fail("Исключение TaskNotFoundException было выброшено: " + e.getMessage());
        }
    }

    @Test
    void createSubtaskShouldCheckEpicExistence() {
        Epic epic = taskManager.createEpic("Test Epic", "Epic description", TaskStatus.NEW);
        Subtask subtask = taskManager.createSubtask("Test Subtask", "Subtask description", TaskStatus.NEW, epic.getId(), Duration.ofHours(1), LocalDateTime.now());

        assertNotNull(subtask, "Подзадача должна быть создана.");
        assertEquals(epic.getId(), subtask.getEpicId(), "Подзадача должна принадлежать эпик.");
    }

    @Test
    void epicStatusShouldBeCalculatedCorrectly() {
        Epic epic = taskManager.createEpic("Test Epic", "Epic description", TaskStatus.NEW);
        Subtask subtask1 = taskManager.createSubtask("Subtask 1", "Description 1", TaskStatus.NEW, epic.getId(), Duration.ofHours(1), LocalDateTime.now());
        Subtask subtask2 = taskManager.createSubtask("Subtask 2", "Description 2", TaskStatus.IN_PROGRESS, epic.getId(), Duration.ofHours(2), LocalDateTime.now().plusHours(1));

        // Обновление статуса эпика
        taskManager.updateEpicStatus(epic.getId());

        // Проверка статуса эпика
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS.");
    }

    @Test
    void shouldCheckTimeIntersection() {
        Task task1 = taskManager.createTask("Task 1", "Description 1", TaskStatus.NEW);
        task1.setStartTime(LocalDateTime.of(2024, 10, 1, 10, 0));
        task1.setDuration(Duration.ofHours(2)); // Задача длится 2 часа

        Task task2 = taskManager.createTask("Task 2", "Description 2", TaskStatus.NEW);
        task2.setStartTime(LocalDateTime.of(2024, 10, 1, 11, 0));
        task2.setDuration(Duration.ofHours(1)); // Задача длится 1 час

        assertTrue(checkIntersection(task1, task2), "Задачи должны пересекаться.");

        // Проверка не пересечения
        Task task3 = taskManager.createTask("Task 3", "Description 3", TaskStatus.NEW);
        task3.setStartTime(LocalDateTime.of(2024, 10, 1, 12, 0));
        task3.setDuration(Duration.ofHours(1)); // Задача длится 1 час

        assertFalse(checkIntersection(task1, task3), "Задачи не должны пересекаться.");
    }
}

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @Test
    void shouldNotConflictWithGeneratedIds() {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = taskManager.createTask("Task 1", "Description 1", TaskStatus.NEW);
        Task task2 = taskManager.createTask("Task 2", "Description 2", TaskStatus.NEW);

        assertNotEquals(task1.getId(), task2.getId(), "Задачи со сгенерированными ID не должны конфликтовать.");
    }

    @Test
    void shouldNotChangeTaskFieldsAfterAdding() {
        TaskManager taskManager = Managers.getDefault();
        Task task = taskManager.createTask("Task", "Description", TaskStatus.NEW);
        int id = task.getId();

        taskManager.updateTask(task); // Обновление задачи

        try {
            Task retrievedTask = taskManager.getTask(id);
            assertEquals("Task", retrievedTask.getName(), "Имя задачи не должно меняться.");
            assertEquals("Description", retrievedTask.getDescription(), "Описание задачи не должно меняться.");
            assertEquals(TaskStatus.NEW, retrievedTask.getStatus(), "Статус задачи не должен меняться.");
        } catch (TaskNotFoundException e) {
            fail("Задача не найдена: " + e.getMessage());
        }
    }

    @Test
    void shouldSavePreviousVersionInHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        TaskManager taskManager = Managers.getDefault();
        Task task = taskManager.createTask("Task", "Description", TaskStatus.NEW);

        taskManager.updateTask(task);
        historyManager.add(task);

        Task previousTask = historyManager.getHistory().stream()
                .filter(t -> t.getId() == task.getId())
                .findFirst()
                .orElse(null);

        assertNotNull(previousTask, "Предыдущую версию задачи необходимо сохранить");
        assertEquals(task.getId(), previousTask.getId(), "Предыдущая версия должна иметь тот же ID.");
    }
}

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File file;

    @BeforeEach
    void setUp() {
        try {
            file = File.createTempFile("taskManagerTest", ".csv");
            file.deleteOnExit();
            taskManager = createTaskManager(); // Инициализация taskManager
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при создании файла", e);
        }
    }

    @Override
    protected FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(file);
    }

    @AfterEach
    void tearDown() {
        if (file != null && file.exists()) {
            file.delete();
        }
    }

    @Test
    void testTaskSavingAndLoading() {
        Task task = taskManager.createTask("Test Task", "Test Description", TaskStatus.NEW);
        assertNotNull(task);
        assertEquals("Test Task", task.getName());

        taskManager.save();
        assertTrue(file.length() > 0, "Файл должен быть не пустым после сохранения");

        try {
            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
            Task loadedTask = loadedManager.getTask(task.getId());
            assertNotNull(loadedTask);
            assertEquals(task.getName(), loadedTask.getName());
            assertEquals(task.getDescription(), loadedTask.getDescription());
            assertEquals(task.getStatus(), loadedTask.getStatus());
        } catch (TaskNotFoundException e) {
            fail("Задача не найдена: " + e.getMessage());
        }
    }

    @Test
    void testLoadFromEmptyFile() {
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        assertTrue(loadedManager.getAllTasks().isEmpty(), "Загруженный менеджер должен быть пуст");
    }
}