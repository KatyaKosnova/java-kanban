package test;

import task.Task;
import taskstatus.TaskStatus;
import taskmanager.HistoryManager;
import taskmanager.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void addAndRemoveTask() {
        Task task = new Task(1, "Test Task", "Description", TaskStatus.NEW);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать одну задачу.");

        historyManager.remove(task.getId());
        history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой после удаления задачи.");
    }

    @Test
    void shouldLimitHistorySize() {
        for (int i = 0; i < 15; i++) {
            Task task = new Task(i, "Task " + i, "Description " + i, TaskStatus.NEW);
            historyManager.add(task);
        }

        List<Task> history = historyManager.getHistory();
        assertEquals(10, history.size(), "История должна быть ограничена 10 задачами.");
    }
}