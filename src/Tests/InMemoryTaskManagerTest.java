package Tests;

import TaskStatus.TaskStatus;
import Task.*;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import TaskManager.*;


class InMemoryTaskManagerTest {

    @Test
    void shouldAddTasksAndFindById() {
        // Создаем менеджер задач
        TaskManager taskManager = Managers.getDefault();

        // Создаем эпик
        Epic epic = taskManager.createEpic("Epic", "Epic description", TaskStatus.NEW);

        // Создаем подзадачу
        Subtask subtask = taskManager.createSubtask("Subtask", "Subtask description", TaskStatus.NEW, epic.getId());

        // Создаем задачу
        Task task = taskManager.createTask("Task", "Task description", TaskStatus.NEW);

        // Проверяем, что эпик создан и доступен
        Epic retrievedEpic = taskManager.getEpic(epic.getId());
        assertNotNull(retrievedEpic, "Эпик надо добавить и найти.");
        assertEquals(epic.getId(), retrievedEpic.getId(), "Полученный ID эпика должен совпадать с ID созданного эпика.");

        // Проверяем, что подзадача создана и доступна
        Subtask retrievedSubtask = taskManager.getSubtask(subtask.getId());
        assertNotNull(retrievedSubtask, "Подзадачу надо добавить и найти.");
        assertEquals(subtask.getId(), retrievedSubtask.getId(), "Полученный ID подзадачи должен совпадать с ID созданной подзадачей.");

        // Проверяем, что задача создана и доступна
        Task retrievedTask = taskManager.getTask(task.getId());
        assertNotNull(retrievedTask, "Задачу надо добавить и найти.");
        assertEquals(task.getId(), retrievedTask.getId(), "Полученный ID задачи должен совпадать с ID задачи.");
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

        taskManager.updateTask(task); // Update the task

        Task retrievedTask = taskManager.getTask(id);
        assertEquals("Task", retrievedTask.getName(), "Имя задачи не должно меняться.");
        assertEquals("Description", retrievedTask.getDescription(), "Описание задачи не должно меняться.");
        assertEquals(TaskStatus.NEW, retrievedTask.getStatus(), "Статус задачи не должен меняться.");
    }

    @Test
    void shouldSavePreviousVersionInHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        TaskManager taskManager = Managers.getDefault();
        Task task = taskManager.createTask("Task", "Description", TaskStatus.NEW);

        taskManager.updateTask(task); // Make changes to task
        historyManager.add(task);

        Task previousTask = historyManager.getHistory().stream()
                .filter(t -> t.getId() == task.getId())
                .findFirst()
                .orElse(null);

        assertNotNull(previousTask, "Предыдущую версию задачи необходимо сохранить");
        assertEquals(task.getId(), previousTask.getId(), "Предыдущая версия должна иметь тот же ID.");
    }
}
