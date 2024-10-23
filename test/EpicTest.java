package test;

import exception.InvalidSubtaskException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import taskstatus.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    private Epic epic;

    @BeforeEach
    void setUp() {
        epic = new Epic(1, "Epic Test", "Description of epic", TaskStatus.NEW);
    }

    @Test
    void shouldAddSubtaskToEpic() throws InvalidSubtaskException {
        Subtask subtask = new Subtask(1, "Subtask 1", "Description of subtask 1", TaskStatus.NEW, epic.getId(), Duration.ofHours(2), LocalDateTime.now());
        epic.addSubtask(subtask);

        assertEquals(1, epic.getSubtasks().size());
        assertEquals(subtask, epic.getSubtasks().get(0));
    }

    @Test
    void shouldNotAddInvalidSubtaskToEpic() {
        Subtask invalidSubtask = new Subtask(2, "Invalid Subtask", "Description of invalid subtask", TaskStatus.NEW, 99, Duration.ofHours(2), LocalDateTime.now());

        InvalidSubtaskException exception = assertThrows(InvalidSubtaskException.class, () -> epic.addSubtask(invalidSubtask));
        assertEquals("Подзадача должна принадлежать эпика.", exception.getMessage());
    }

    @Test
    void shouldRemoveSubtaskFromEpic() throws InvalidSubtaskException {
        Subtask subtask = new Subtask(1, "Subtask 1", "Description of subtask 1", TaskStatus.NEW, epic.getId(), Duration.ofHours(2), LocalDateTime.now());
        epic.addSubtask(subtask);
        epic.removeSubtask(subtask);

        assertTrue(epic.getSubtasks().isEmpty());
    }

    @Test
    void shouldUpdateSubtaskInEpic() throws InvalidSubtaskException {
        Subtask subtask = new Subtask(1, "Subtask 1", "Description of subtask 1", TaskStatus.NEW, epic.getId(), Duration.ofHours(2), LocalDateTime.now());
        epic.addSubtask(subtask);

        Subtask updatedSubtask = new Subtask(1, "Subtask 1 Updated", "Updated description", TaskStatus.IN_PROGRESS, epic.getId(), Duration.ofHours(3), LocalDateTime.now());
        epic.updateSubtask(updatedSubtask);

        assertEquals(updatedSubtask, epic.getSubtasks().get(0));
    }

    @Test
    void shouldUpdateEpicStatusBasedOnSubtasks() throws InvalidSubtaskException {
        Subtask subtask1 = new Subtask(1, "Subtask 1", "Description of subtask 1", TaskStatus.NEW, epic.getId(), Duration.ofHours(2), LocalDateTime.now());
        Subtask subtask2 = new Subtask(2, "Subtask 2", "Description of subtask 2", TaskStatus.DONE, epic.getId(), Duration.ofHours(1), LocalDateTime.now());

        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);
        epic.updateEpicStatus();

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus()); // В эпике есть одна новая и одна завершенная подзадача
    }

    @Test
    void shouldCalculateEpicDurationAndTime() throws InvalidSubtaskException {
        Subtask subtask1 = new Subtask(1, "Subtask 1", "Description of subtask 1", TaskStatus.DONE, epic.getId(), Duration.ofHours(2), LocalDateTime.of(2024, 10, 1, 9, 0));
        Subtask subtask2 = new Subtask(2, "Subtask 2", "Description of subtask 2", TaskStatus.DONE, epic.getId(), Duration.ofHours(3), LocalDateTime.of(2024, 10, 1, 12, 0));

        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);
        epic.updateEpicProperties();

        assertEquals(Duration.ofHours(5), epic.getDuration());
        assertEquals(LocalDateTime.of(2024, 10, 1, 9, 0), epic.getStartTime());
        assertEquals(LocalDateTime.of(2024, 10, 1, 15, 0), epic.getEndTime());
    }

    @Test
    void shouldNotRemoveInvalidSubtaskFromEpic() throws InvalidSubtaskException {
        Subtask subtask = new Subtask(1, "Subtask 1", "Description of subtask 1", TaskStatus.NEW, epic.getId(), Duration.ofHours(2), LocalDateTime.now());
        epic.addSubtask(subtask);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            Subtask invalidSubtask = new Subtask(2, "Invalid Subtask", "Description of invalid subtask", TaskStatus.NEW, 99, Duration.ofHours(2), LocalDateTime.now());
            epic.removeSubtask(invalidSubtask);
        });
        assertEquals("Подзадача должна принадлежать эпика.", exception.getMessage());
    }

    @Test
    void shouldUpdateEpicStatusWhenAllSubtasksAreNew() throws InvalidSubtaskException {
        Subtask subtask1 = new Subtask(1, "Subtask 1", "Description", TaskStatus.NEW, epic.getId(), Duration.ofHours(1), LocalDateTime.now());
        Subtask subtask2 = new Subtask(2, "Subtask 2", "Description", TaskStatus.NEW, epic.getId(), Duration.ofHours(1), LocalDateTime.now());

        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);
        epic.updateEpicStatus();

        assertEquals(TaskStatus.NEW, epic.getStatus()); // Все подзадачи NEW
    }

    @Test
    void shouldUpdateEpicStatusWhenAllSubtasksAreDone() throws InvalidSubtaskException {
        Subtask subtask1 = new Subtask(1, "Subtask 1", "Description", TaskStatus.DONE, epic.getId(), Duration.ofHours(1), LocalDateTime.now());
        Subtask subtask2 = new Subtask(2, "Subtask 2", "Description", TaskStatus.DONE, epic.getId(), Duration.ofHours(1), LocalDateTime.now());

        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);
        epic.updateEpicStatus();

        assertEquals(TaskStatus.DONE, epic.getStatus()); // Все подзадачи DONE
    }

    @Test
    void shouldUpdateEpicStatusWhenSubtasksAreNewAndDone() throws InvalidSubtaskException {
        Subtask subtask1 = new Subtask(1, "Subtask 1", "Description", TaskStatus.NEW, epic.getId(), Duration.ofHours(1), LocalDateTime.now());
        Subtask subtask2 = new Subtask(2, "Subtask 2", "Description", TaskStatus.DONE, epic.getId(), Duration.ofHours(1), LocalDateTime.now());

        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);
        epic.updateEpicStatus();

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus()); // Подзадачи NEW и DONE
    }

    @Test
    void shouldUpdateEpicStatusWhenSubtasksAreInProgress() throws InvalidSubtaskException {
        Subtask subtask1 = new Subtask(1, "Subtask 1", "Description", TaskStatus.IN_PROGRESS, epic.getId(), Duration.ofHours(1), LocalDateTime.now());
        Subtask subtask2 = new Subtask(2, "Subtask 2", "Description", TaskStatus.DONE, epic.getId(), Duration.ofHours(1), LocalDateTime.now());

        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);
        epic.updateEpicStatus();

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus()); // Подзадача в статусе IN_PROGRESS
    }
}