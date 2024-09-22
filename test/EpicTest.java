package test;

import task.Epic;
import task.Subtask;
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

        // Проверяем, что добавление подзадачи с тем же идентификатором, что и у эпика, выбросит исключение
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            taskManager.createSubtask("Подзадача", "", TaskStatus.NEW, epic.getId());
        });

        // Проверка сообщения об ошибке
        assertEquals("Эпик не может быть подзадачей самого себя.", exception.getMessage());

        // Проверка, что подзадач еще нет в эпике
        assertTrue(epic.getSubtasks().isEmpty(), "Эпик не должен содержать подзадач.");

        // Проверка статуса эпика
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус эпика должен оставаться NEW после попытки добавить подзадачу.");
    }

    @Test
    void shouldUpdateEpicCorrectly() {
        // Создаем эпик
        Epic epic = taskManager.createEpic("Epic", "Initial description", TaskStatus.NEW);

        // Обновляем описание эпика
        epic.setDescription("Updated description");

        // Проверяем обновление через менеджер
        assertDoesNotThrow(() -> taskManager.updateEpic(epic), "Не должно быть исключения при обновлении эпика.");

        // Проверяем состояние эпика
        Epic updatedEpic = taskManager.getEpicById(epic.getId());
        assertNotNull(updatedEpic, "Обновленный эпик не должен быть null.");
        assertEquals("Updated description", updatedEpic.getDescription(), "Описание эпика должно быть обновлено.");
        assertEquals(TaskStatus.NEW, updatedEpic.getStatus(), "Статус эпика должен оставаться NEW после обновления.");
    }
}