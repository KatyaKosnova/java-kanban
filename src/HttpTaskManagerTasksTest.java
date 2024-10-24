import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import taskmanager.InMemoryTaskManager;
import taskmanager.TaskManager;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerTasksTest {
    private HttpTaskServer httpTaskServer;
    private TaskManager taskManager;
    private Gson gson;

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = new InMemoryTaskManager();
        httpTaskServer = new HttpTaskServer(taskManager);
        gson = new Gson();
        httpTaskServer.start();
    }

    @AfterEach
    public void tearDown() {
        httpTaskServer.stop();
    }

    @Test
    public void testServerIsRunning() throws IOException {
        URL url = new URL("http://localhost:8085/tasks");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();
        assertEquals(HttpURLConnection.HTTP_OK, responseCode, "Сервер должен возвращать статус 200 при GET-запросе на /tasks");
    }

    @Test
    public void testGetTasksEmpty() throws IOException {
        URL url = new URL("http://localhost:8085/tasks");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();
        assertEquals(HttpURLConnection.HTTP_OK, responseCode);

        String responseBody;
        try (Scanner scanner = new Scanner(connection.getInputStream())) {
            responseBody = scanner.useDelimiter("\\A").next();
        }

        assertEquals("[]", responseBody, "Список задач должен быть пустым при начальной инициализации");
    }

}
