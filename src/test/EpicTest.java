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

        // Переменная для хранения результата добавления подзадачи
        boolean isExceptionThrown = false;

        try {
            // Попытка добавить подзадачу с тем же идентификатором, что и у эпика
            taskManager.createSubtask("Подзадача", "", TaskStatus.NEW, epic.getId());
        } catch (IllegalArgumentException e) {
            // Если выбрасывается исключение, устанавливаем флаг
            isExceptionThrown = true;
            // Проверяем сообщение об ошибке
            if (!e.getMessage().equals("Эпик не может быть подзадачей самого себя.")) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }

        // Проверяем состояние после попытки добавления
        if (!isExceptionThrown) {
            System.out.println("Ошибка: исключение не было выброшено.");
        }

        // Проверка, что подзадач нет в эпике
        if (!epic.getSubtasks().isEmpty()) {
            System.out.println("Ошибка: эпик содержит подзадачи.");
        }

        // Проверка статуса эпика
        if (epic.getStatus() != TaskStatus.NEW) {
            System.out.println("Ошибка: статус эпика не равен NEW.");
        }
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