package test;

import task.Task;
import taskstatus.TaskStatus;
import taskmanager.HistoryManager;
import taskmanager.Managers;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class HistoryManagerTest {

    @Test
    void addAndRetrieveHistory() {
        // Получаем экземпляр менеджера истории
        HistoryManager historyManager = Managers.getDefaultHistory();

        // Создаем задачу
        Task task = new Task(1, "Test Task", "Description", TaskStatus.NEW); // Используем конструктор с id
        historyManager.add(task);

        // Получаем историю
        final List<Task> history = historyManager.getHistory();

        // Проверяем, что история не пуста и содержит одну задачу
        assertNotNull(history, "История не должна быть пустой.");
        assertEquals(1, history.size(), "История должна содержать одну задачу.");
        assertEquals(task, history.get(0), "Задача в истории не соответствует добавленной.");

        // Добавляем еще одну задачу и проверяем историю
        Task anotherTask = new Task(2, "Another Task", "Another description", TaskStatus.NEW);
        historyManager.add(anotherTask);

        final List<Task> updatedHistory = historyManager.getHistory();

        // Проверяем, что история содержит две задачи
        assertNotNull(updatedHistory, "История не должна быть пустой.");
        assertEquals(2, updatedHistory.size(), "История должна содержать две задачи.");
        assertEquals(task, updatedHistory.get(0), "Первая задача в истории не соответствует добавленной.");
        assertEquals(anotherTask, updatedHistory.get(1), "Вторая задача в истории не соответствует добавленной.");
    }
}