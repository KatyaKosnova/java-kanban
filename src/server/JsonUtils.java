package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtils {
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting() // Пример настройки: отступы для удобного отображения
            .create();
}