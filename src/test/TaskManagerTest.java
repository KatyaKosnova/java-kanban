package test;

import task.Task;
import taskstatus.TaskStatus;
import taskmanager.Managers;
import taskmanager.TaskManager;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class TaskManagerTest {

    @Test
    void addNewTask() {

        TaskManager taskManager = Managers.getDefault();

        // Создание задачи
        Task task = taskManager.createTask("Test addNewTask", "Test addNewTask description", TaskStatus.NEW);
        final int taskId = task.getId(); // Получаем id созданной задачи

        // Добавление задачи в менеджер
        taskManager.updateTask(task);

        // Получение задачи из менеджера
        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        // Получение всех задач
        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }
}
