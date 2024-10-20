package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import taskmanager.TaskManager;
import task.Epic;

import java.io.IOException;
import java.io.InputStreamReader;

public class EpicHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public EpicHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    // Логика получения всех эпиков
                    String epicsJson = gson.toJson(taskManager.getAllEpics());
                    sendJson(exchange, epicsJson, 200);
                    break;
                case "POST":
                    // Логика создания нового эпика
                    Epic newEpic = gson.fromJson(new InputStreamReader(exchange.getRequestBody()), Epic.class);
                    taskManager.createEpic(newEpic.getName(), newEpic.getDescription(), newEpic.getStatus());
                    sendText(exchange, "Эпик создан", 201);
                    break;
                case "DELETE":
                    // Логика удаления эпика
                    int epicId = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
                    taskManager.deleteEpic(epicId);
                    sendText(exchange, "Эпик удален", 200);
                    break;
                default:
                    sendNotFound(exchange);
                    break;
            }
        } catch (Exception e) {
            handleError(exchange, e);
        }
    }
}