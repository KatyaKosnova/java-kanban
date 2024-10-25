package server;

import com.sun.net.httpserver.HttpServer;
import taskmanager.InMemoryTaskManager;
import taskmanager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8085;
    private final HttpServer httpServer;
    private final TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        this.httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        initContexts();
    }

    private void initContexts() {
        // Регистрация обработчиков для разных эндпоинтов, передаем JsonUtils.GSON
        httpServer.createContext("/tasks", new TaskHandler(taskManager, JsonUtils.GSON));
        httpServer.createContext("/tasks/epic", new EpicHandler(taskManager, JsonUtils.GSON));
        httpServer.createContext("/tasks/subtask", new SubtaskHandler(taskManager, JsonUtils.GSON));
        httpServer.createContext("/tasks/prioritized", new PrioritizedTaskHandler(taskManager, JsonUtils.GSON));
        httpServer.createContext("/tasks/history", new HistoryHandler(taskManager, JsonUtils.GSON));
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
            TaskManager taskManager = new InMemoryTaskManager();
            HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
            httpTaskServer.start();
        } catch (IOException e) {
            System.err.println("Ошибка при запуске сервера: " + e.getMessage());
        }
    }
}