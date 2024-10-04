package test;

import task.Epic;
import taskstatus.TaskStatus;
import taskmanager.TaskManager;
import taskmanager.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {

    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
    }

    @Test
    void shouldNotAddSelfAsSubtask() {
        // Создаем эпик
        Epic epic = taskManager.createEpic("Эпик", " ", TaskStatus.NEW);

        // Проверяем, что выбрасывается исключение при добавлении подзадачи с тем же идентификатором, что и у эпика
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            // Попытка добавить подзадачу с тем же идентификатором, что и у эпика
            taskManager.createSubtask("Подзадача", "", TaskStatus.NEW, epic.getId());
        });

        // Проверяем сообщение об ошибке
        assertEquals("Эпик не может быть подзадачей самого себя.", exception.getMessage());

        // Проверяем, что подзадачи не добавлены
        assertTrue(epic.getSubtasks().isEmpty(), "Эпик не должен содержать подзадач.");

        // Проверяем, что статус эпика остался NEW
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус эпика должен оставаться NEW.");
    }

    @Test
    void shouldUpdateEpicCorrectly() {
        // Создаем эпик
        Epic epic = taskManager.createEpic("Epic", "Initial description", TaskStatus.NEW);

        // Обновляем описание эпика
        epic.setDescription("Updated description");

        // Проверяем обновление через менеджер
        taskManager.updateEpic(epic);

        // Проверяем состояние эпика
        Epic updatedEpic = taskManager.getEpic(epic.getId());
        assertNotNull(updatedEpic, "Обновленный эпик не должен быть null.");
        assertEquals("Updated description", updatedEpic.getDescription(), "Описание эпика должно быть обновлено.");
        assertEquals(TaskStatus.NEW, updatedEpic.getStatus(), "Статус эпика должен оставаться NEW после обновления.");
    }
}