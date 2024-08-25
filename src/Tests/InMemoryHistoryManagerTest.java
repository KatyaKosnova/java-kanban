package Tests;
import Task.Task;
import TaskStatus.TaskStatus;
import TaskManager.HistoryManager;
import TaskManager.Managers;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import TaskManager.*;

class InMemoryHistoryManagerTest {

    @Test
    void shouldLimitHistorySize() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        TaskManager taskManager = Managers.getDefault();

        for (int i = 0; i < 15; i++) {
            Task task = taskManager.createTask("Task " + i, "Description " + i, TaskStatus.NEW);
            historyManager.add(task);
        }

        assertEquals(10, historyManager.getHistory().size(), "Размер истории должен быть ограничен 10.");
    }
}