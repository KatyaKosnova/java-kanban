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
                    sendResponse(exchange, 405, "Метод не разрешен"); // Метод не разрешен
            }
        }

        // Метод для обработки POST-запросов на добавление задачи
        private void handlePostRequest(HttpExchange exchange) throws IOException {
            InputStream inputStream = exchange.getRequestBody();
            Task task = gson.fromJson(new InputStreamReader(inputStream), Task.class);

            if (task != null) {
                taskManager.addTask(task);
                sendResponse(exchange, 201, "Задача добавлена");
            } else {
                sendResponse(exchange, 400, "Неверные данные");
            }
        }

        // Метод для обработки GET-запросов на получение всех задач
        private void handleGetRequest(HttpExchange exchange) throws IOException {
            String allTasksJson = gson.toJson(taskManager.getAllTasks());
            sendResponse(exchange, 200, allTasksJson);
        }

        // Метод для обработки DELETE-запросов на удаление задачи по идентификатору
        private void handleDeleteRequest(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            if (query != null && query.startsWith("id=")) {
                String idString = query.substring(3);
                try {
                    int id = Integer.parseInt(idString);
                    taskManager.deleteTask(id);
                    sendResponse(exchange, 200, "Задача удалена");
                } catch (NumberFormatException e) {
                    sendResponse(exchange, 400, "Неверный идентификатор"); // Неверный идентификатор
                } catch (TaskNotFoundException e) {
                    sendResponse(exchange, 404, "Задача не найдена"); // Задача не найдена
                }
            } else {
                sendResponse(exchange, 400, "Неверный запрос"); // Неверный запрос
            }
        }

        // Метод для отправки ответа клиенту
        private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
            exchange.sendResponseHeaders(statusCode, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes(StandardCharsets.UTF_8));
            }
        }
    }
}
