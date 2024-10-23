package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import task.Task;
import taskmanager.InMemoryTaskManager;
import taskmanager.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private HttpServer httpServer;
    private Gson gson; // Создаем экземпляр Gson здесь
    private TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        this.gson = new Gson(); // Создаем экземпляр Gson
        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler(taskManager, gson));
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager, gson));
        httpServer.createContext("/epics", new EpicHandler(taskManager, gson));
        httpServer.createContext("/history", new HistoryHandler(taskManager, gson));
        httpServer.createContext("/prioritized", new PrioritizedTaskHandler(taskManager, gson));
    }

    public void start() {
        System.out.println("Сервер запущен на порту: " + PORT);
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("Сервер остановлен на порту: " + PORT);
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer(new InMemoryTaskManager());
        httpTaskServer.start();
    }

    // Определение класса TaskHandler
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
                    InputStream inputStream = exchange.getRequestBody();
                    Task task = gson.fromJson(new InputStreamReader(inputStream), Task.class);
                    taskManager.addTask(task);
                    String response = "Задача добавлена";
                    exchange.sendResponseHeaders(201, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                    break;
                case "GET":
                    // Пример обработки GET запроса
                    // Получить все задачи и вернуть их
                    String allTasksJson = gson.toJson(taskManager.getAllTasks());
                    exchange.sendResponseHeaders(200, allTasksJson.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(allTasksJson.getBytes());
                    }
                    break;
                default:
                    exchange.sendResponseHeaders(405, -1); // Метод не разрешен
            }
        }
    }
}
