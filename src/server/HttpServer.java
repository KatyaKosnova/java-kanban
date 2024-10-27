package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class HttpServer {
    private final int port;

    public HttpServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Сервер запущен на порту: " + port);

        while (true) {
            try (Socket clientSocket = serverSocket.accept()) {
                handleClient(clientSocket);
            } catch (IOException e) {
                System.out.println("Ошибка при обработке клиента: " + e.getMessage());
            }
        }
    }

    private void handleClient(Socket clientSocket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        String requestLine = in.readLine();
        if (requestLine == null) return;

        String[] requestParts = requestLine.split(" ");
        String method = requestParts[0];
        String path = requestParts[1];

        // Простой пример обработки GET и POST
        if ("GET".equalsIgnoreCase(method)) {
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: text/plain");
            out.println();
            out.println("Запрос обработан: " + path);
        } else if ("POST".equalsIgnoreCase(method)) {
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: text/plain");
            out.println();
            out.println("POST-запрос обработан: " + path);
        } else {
            out.println("HTTP/1.1 405 Method Not Allowed");
            out.println();
        }
    }

    public static void main(String[] args) {
        HttpServer server = new HttpServer(8085);
        try {
            server.start();
        } catch (IOException e) {
            System.out.println("Ошибка запуска сервера: " + e.getMessage());
        }
    }
}