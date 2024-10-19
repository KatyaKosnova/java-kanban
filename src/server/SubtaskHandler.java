package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import task.Subtask;
import taskmanager.TaskManager;

import java.io.IOException;
import java.io.InputStreamReader;

public class SubtaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public SubtaskHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET":
                // Логика получения всех подзадач
                String subtasksJson = gson.toJson(taskManager.getAllSubtasks());
                sendText(exchange, subtasksJson, 200);
                break;
            case "POST":
                // Логика создания новой подзадачи
                Subtask newSubtask = gson.fromJson(new InputStreamReader(exchange.getRequestBody()), Subtask.class);
                taskManager.createSubtask(newSubtask.getName(), newSubtask.getDescription(), newSubtask.getStatus(), newSubtask.getEpicId());
                sendText(exchange, "Подзадача создана", 201);
                break;
            case "DELETE":
                // Логика удаления подзадачи
                int subtaskId = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
                if (taskManager.getAllSubtasks().stream().anyMatch(subtask -> subtask.getId() == subtaskId)) {
                    taskManager.deleteSubtask(subtaskId);
                    sendText(exchange, "Подзадача удалена", 200);
                } else {
                    sendNotFound(exchange); // Подзадача не найдена
                }
                break;
            default:
                sendNotFound(exchange);
                break;
        }
    }
}