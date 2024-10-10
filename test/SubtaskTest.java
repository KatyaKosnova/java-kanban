package task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskstatus.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    private Subtask subtask;

    @BeforeEach
    void setUp() {
        // Инициализация подзадачи перед каждым тестом
        int id = 1;
        String name = "Test Subtask";
        String description = "Description of the test subtask";
        TaskStatus status = TaskStatus.NEW;
        int epicId = 101;
        Duration duration = Duration.ofHours(2);
        LocalDateTime startTime = LocalDateTime.of(2024, 10, 8, 10, 0);

        subtask = new Subtask(id, name, description, status, epicId, duration, startTime);
    }

    @Test
    void testConstructor() {
        assertEquals(1, subtask.getId());
        assertEquals("Test Subtask", subtask.getName());
        assertEquals("Description of the test subtask", subtask.getDescription());
        assertEquals(TaskStatus.NEW, subtask.getStatus());
        assertEquals(101, subtask.getEpicId());
        assertEquals(Duration.ofHours(2), subtask.getDuration());
        assertEquals(LocalDateTime.of(2024, 10, 8, 10, 0), subtask.getStartTime());
        assertEquals(LocalDateTime.of(2024, 10, 8, 12, 0), subtask.getEndTime()); // Время окончания должно быть 2 часа позже
    }

    @Test
    void testCalculateEndTime() {
        LocalDateTime expectedEndTime = LocalDateTime.of(2024, 10, 8, 12, 0);
        assertEquals(expectedEndTime, subtask.getEndTime());
    }

    @Test
    void testEquals() {
        Subtask anotherSubtask = new Subtask(1, "Test Subtask", "Description of the test subtask",
                TaskStatus.NEW, 101, Duration.ofHours(2), LocalDateTime.of(2024, 10, 8, 10, 0));
        assertEquals(subtask, anotherSubtask);
    }

    @Test
    void testNotEquals() {
        Subtask differentSubtask = new Subtask(2, "Different Subtask", "Different description",
                TaskStatus.NEW, 102, Duration.ofHours(1), LocalDateTime.of(2024, 10, 8, 11, 0));
        assertNotEquals(subtask, differentSubtask);
    }

    @Test
    void testHashCode() {
        Subtask anotherSubtask = new Subtask(1, "Test Subtask", "Description of the test subtask",
                TaskStatus.NEW, 101, Duration.ofHours(2), LocalDateTime.of(2024, 10, 8, 10, 0));
        assertEquals(subtask.hashCode(), anotherSubtask.hashCode());
    }

    @Test
    void testToString() {
        String expectedString = "Subtask{id=1, name='Test Subtask', description='Description of the test subtask', " +
                "status=NEW, epicId=101, duration=PT2H, startTime=2024-10-08T10:00, endTime=2024-10-08T12:00}";
        assertEquals(expectedString, subtask.toString());
    }
}