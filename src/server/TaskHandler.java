package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import task.Task;
import taskmanager.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TaskHandler extends BaseHttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public TaskHandler(TaskManager taskManager, Gson gson) {
        super(); // Вызов конструктора родительского класса
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "POST":
                handlePost(exchange);
                break;
            case "GET":
                handleGet(exchange);
                break;
            case "DELETE":
                handleDelete(exchange);
                break;
            default:
                sendText(exchange, "Метод не разрешен", 405);
                break;
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        // Обработка запроса на добавление задачи
        try (InputStream inputStream = exchange.getRequestBody()) {
            Task task = gson.fromJson(new InputStreamReader(inputStream), Task.class);
            taskManager.addTask(task);
            String response = gson.toJson(task); // Возвращаем добавленную задачу
            sendJson(exchange, response, 201);
        } catch (JsonSyntaxException e) {
            sendText(exchange, "Неверный формат данных задачи", 400);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        // Логика для получения списка задач
        String response = gson.toJson(taskManager.getAllTasks());
        sendJson(exchange, response, 200);
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query != null && query.startsWith("id=")) {
            String taskIdString = query.split("=")[1];
            try {
                int taskId = Integer.parseInt(taskIdString); // Преобразование в int
                taskManager.deleteTask(taskId);
                exchange.sendResponseHeaders(204, -1); // No Content
            } catch (NumberFormatException e) {
                sendText(exchange, "Неверный формат идентификатора задачи", 400);
            } catch (taskmanager.TaskNotFoundException e) {
                sendText(exchange, "Задача с идентификатором " + taskIdString + " не найдена", 404);
            }
        } else {
            sendText(exchange, "Идентификатор задачи не указан", 400);
        }
    }
}