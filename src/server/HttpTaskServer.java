package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import task.Task;
import taskmanager.TaskManager;
import taskmanager.TaskNotFoundException;
import taskmanager.InMemoryTaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class HttpTaskServer {
    private static final int PORT = 8085;
    private final HttpServer httpServer;
    private final Gson gson;
    private final TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        this.gson = new Gson();
        this.httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        initContexts();
    }

    private void initContexts() {
        // Регистрация обработчиков для разных эндпоинтов
        httpServer.createContext("/tasks", new TaskHandler(taskManager, gson));
        // Здесь можно добавить обработчики для других типов задач (epics, subtasks и т.д.)
    }

    public void start() {
        System.out.println("Сервер запущен на порту: " + PORT);
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("Сервер остановлен на порту: " + PORT);
    }

    public static void main(String[] args) {
        try {
            TaskManager taskManager = new InMemoryTaskManager(); // Инициализация менеджера задач
            HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
            httpTaskServer.start();
        } catch (IOException e) {
            System.err.println("Ошибка при запуске сервера: " + e.getMessage());
        }
    }

    // Вложенный класс для обработки HTTP-запросов
    public static class TaskHandler implements HttpHandler {
        private final TaskManager taskManager;
        private final Gson gson;

        public TaskHandler(TaskManager taskManager, Gson gson) {
            this.taskManager = taskManager;
            this.gson = gson;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            switch (exchange.getRequestMethod()) {
                case "POST":
                    handlePostRequest(exchange);
                    break;
                case "GET":
                    handleGetRequest(exchange);
                    break;
                case "DELETE":
                    handleDeleteRequest(exchange);
                    break;
                default:
                    exchange.sendResponseHeaders(405, -1); // Метод не разрешен
            }
        }

        // Метод для обработки POST-запросов на добавление задачи
        private void handlePostRequest(HttpExchange exchange) throws IOException {
            InputStream inputStream = exchange.getRequestBody();
            Task task = gson.fromJson(new InputStreamReader(inputStream), Task.class);

            if (task != null) {
                taskManager.addTask(task);
                String response = "Задача добавлена";
                exchange.sendResponseHeaders(201, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                }
            } else {
                exchange.sendResponseHeaders(400, -1); // Неверные данные
            }
        }

        // Метод для обработки GET-запросов на получение всех задач
        private void handleGetRequest(HttpExchange exchange) throws IOException {
            String allTasksJson = gson.toJson(taskManager.getAllTasks());
            exchange.sendResponseHeaders(200, allTasksJson.getBytes(StandardCharsets.UTF_8).length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(allTasksJson.getBytes(StandardCharsets.UTF_8));
            }
        }

        // Метод для обработки DELETE-запросов на удаление задачи по идентификатору
        private void handleDeleteRequest(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            if (query != null && query.startsWith("id=")) {
                String idString = query.substring(3);
                try {
                    int id = Integer.parseInt(idString);
                    taskManager.deleteTask(id);
                    String response = "Задача удалена";
                    exchange.sendResponseHeaders(200, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes(StandardCharsets.UTF_8));
                    }
                } catch (NumberFormatException e) {
                    exchange.sendResponseHeaders(400, -1); // Неверный идентификатор
                } catch (TaskNotFoundException e) {
                    exchange.sendResponseHeaders(404, -1); // Задача не найдена
                }
            } else {
                exchange.sendResponseHeaders(400, -1); // Неверный запрос
            }
        }
    }
}
