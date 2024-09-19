package test;

import task.Task;
import taskstatus.TaskStatus;
import taskmanager.InMemoryHistoryManager;
import taskmanager.HistoryManager;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class InMemoryHistoryManagerTest {

    @Test
    void addAndRemoveTask() {

        HistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task(1, "Test Task", "Description", TaskStatus.NEW);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать одну задачу.");

        historyManager.remove(task.getId());
        history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой.");
    }

    @Test
    void shouldLimitHistorySize() {

        HistoryManager historyManager = new InMemoryHistoryManager();

        for (int i = 0; i < 15; i++) {
            Task task = new Task(i, "Task " + i, "Description " + i, TaskStatus.NEW);
            historyManager.add(task);
        }

        List<Task> history = historyManager.getHistory();
        assertEquals(10, history.size(), "История должна быть ограничена 10 задачами.");
    }
}