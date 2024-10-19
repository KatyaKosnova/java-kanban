package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import taskmanager.TaskManager;

import java.io.IOException;

public class PrioritizedTaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public PrioritizedTaskHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            // Логика получения приоритета задач
            String prioritizedTasksJson = gson.toJson(taskManager.getAllTasks());
            sendJson(exchange, prioritizedTasksJson, 200);
        } else {
            sendNotFound(exchange);
        }
    }
}