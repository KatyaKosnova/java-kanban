package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import taskmanager.TaskManager;
import taskmanager.InMemoryTaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

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
        httpServer.createContext("/tasks/epic", new EpicHandler(taskManager, gson));
        httpServer.createContext("/tasks/subtask", new SubtaskHandler(taskManager, gson));
        httpServer.createContext("/tasks/prioritized", new PrioritizedTaskHandler(taskManager, gson));
        httpServer.createContext("/tasks/history", new HistoryHandler(taskManager, gson));
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
}
