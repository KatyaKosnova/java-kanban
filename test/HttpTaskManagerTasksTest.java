import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class HttpTaskManagerTasksTest {
    private static final String BASE_URL = "http://localhost:8080";

    @BeforeAll
    public static void startServer() {
        // Запуск сервера в отдельном потоке
        new Thread(() -> {
            try {
                HttpTaskServer.main(new String[]{});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();


        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    @Test
    public void testAddTask() {
        String taskJson = "{\"name\":\"Test Task\", \"description\":\"This is a test task.\"}";

        try {
            URL url = new URL(BASE_URL + "/tasks");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            try (var writer = new java.io.OutputStreamWriter(connection.getOutputStream())) {
                writer.write(taskJson);
                writer.flush();
            }

            int responseCode = connection.getResponseCode();
            assertEquals(201, responseCode, "Код ответа не соответствует ожидаемому");

        } catch (IOException e) {
            e.printStackTrace(); // Вывод стека ошибки
            fail ("Ошибка при выполнении запроса: " + e.getMessage());
        }
    }
}