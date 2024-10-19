package server;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {

    protected void sendText(HttpExchange exchange, String responseText, int responseCode) throws IOException {
        byte[] responseBytes = responseText.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(responseCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    protected void sendJson(HttpExchange exchange, String jsonResponse, int responseCode) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        sendText(exchange, jsonResponse, responseCode);
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        sendText(exchange, "Не найдено", 404);
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        sendText(exchange, "Задача пересекается с существующими задачами", 409);
    }

    protected void handleError(HttpExchange exchange, Exception e) throws IOException {
        // Здесь можно добавить более детальную обработку ошибок, например, логирование
        sendText(exchange, "Внутренняя ошибка сервера: " + e.getMessage(), 500);
    }
}