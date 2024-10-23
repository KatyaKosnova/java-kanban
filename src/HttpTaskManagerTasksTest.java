import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerTasksTest {
    private static final String BASE_URL = "http://localhost:8085";

    @BeforeAll
    public static void startServer() {
        // Запуск сервера в отдельном потоке
        Thread serverThread = new Thread(() -> {
            try {
                HttpTaskServer.main(new String[]{});
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        serverThread.start();

        // Проверка, запущен ли сервер
        boolean serverUp = false;
        for (int i = 0; i < 15; i++) { // Проверяем 15 раз с интервалом 1 секунда
            try {
                Thread.sleep(1000); // Ждем 1 секунду
                HttpURLConnection connection = (HttpURLConnection) new URL(BASE_URL).openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(1000);
                connection.connect();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    serverUp = true;
                    break; // Сервер запущен, выходим из цикла
                }
            } catch (IOException e) {
                // Сервер еще не готов, продолжаем проверять
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Поток был прерван", e);
            }
        }

        if (!serverUp) {
            throw new IllegalStateException("Сервер не запустился в течение 15 секунд");
        }
    }

    @Test
    public void testAddTask() throws Exception {
        URL url = new URL(BASE_URL + "/tasks");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        String jsonInputString = "{\"name\":\"New Task\",\"description\":\"Task Description\"}";
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        assertEquals(HttpURLConnection.HTTP_OK, responseCode);

        // Обработка ответа сервера
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            // Дополнительные проверки ответа могут быть здесь
        }
    }
}
