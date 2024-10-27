import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import taskmanager.InMemoryTaskManager;
import taskmanager.TaskManager;
import server.JsonUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerTasksTest {
    private HttpTaskServer httpTaskServer;
    private TaskManager taskManager;
    private final Gson gson = JsonUtils.GSON;

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = new InMemoryTaskManager();
        httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
    }

    @AfterEach
    public void tearDown() {
        httpTaskServer.stop();
    }

    @Test
    public void testServerIsRunning() throws IOException {
        HttpURLConnection connection = createConnection("http://localhost:8085/tasks", "GET");
        int responseCode = getResponseCode(connection);
        assertEquals(HttpURLConnection.HTTP_OK, responseCode, "Сервер должен возвращать статус 200 при GET-запросе на /tasks");
    }

    @Test
    public void testGetTasksEmpty() throws IOException {
        HttpURLConnection connection = createConnection("http://localhost:8085/tasks", "GET");
        int responseCode = getResponseCode(connection);
        assertEquals(HttpURLConnection.HTTP_OK, responseCode);

        String responseBody = getResponseBody(connection);
        assertEquals("[]", responseBody, "Список задач должен быть пустым при начальной инициализации");
    }

    private HttpURLConnection createConnection(String urlString, String requestMethod) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(requestMethod);
        return connection;
    }

    private int getResponseCode(HttpURLConnection connection) throws IOException {
        return connection.getResponseCode();
    }

    private String getResponseBody(HttpURLConnection connection) throws IOException {
        try (Scanner scanner = new Scanner(connection.getInputStream())) {
            return scanner.useDelimiter("\\A").next();
        }
    }
}