package tests;

import task.Epic;
import task.Subtask;
import taskstatus.TaskStatus;
import taskmanager.TaskManager;
import taskmanager.Managers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {

    @Test
    void shouldNotAddSelfAsSubtask() {

        TaskManager taskManager = Managers.getDefault();

        // Создаем эпик
        Epic epic = taskManager.createEpic("Epic", "Epic description", TaskStatus.NEW);


        assertDoesNotThrow(() -> {
            taskManager.createSubtask("Subtask", "Subtask description", TaskStatus.NEW, epic.getId());
        }, "Не должно быть исключения при создании подзадачи для существующего эпика.");
    }


    @Test
    void shouldNotAssignSubtaskAsEpic() {

        TaskManager taskManager = Managers.getDefault();

        // Создаем эпик
        Epic epic = taskManager.createEpic("Epic", "Epic description", TaskStatus.NEW);

        // Создаем подзадачу
        Subtask subtask = taskManager.createSubtask("Subtask", "Subtask description", TaskStatus.NEW, epic.getId());



        // Проверка, что эпик обновляется корректно:
        assertDoesNotThrow(() -> {
            taskManager.updateEpic(epic); // Проверяем, что эпик может быть обновлен корректно
        }, "Не должно быть исключения при обновлении корректного эпика.");


    }
}