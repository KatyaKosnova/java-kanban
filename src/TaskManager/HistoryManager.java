package taskmanager;

import task.Task;
import java.util.List;

public interface HistoryManager {

    void add(Task task);          // Добавление задачи в историю

    void remove(int id);          // Удаление задачи по ID

    List<Task> getHistory();

    void clear(); // Метод для очистки истории
    // Метод для получения истории задач// Получение истории просмотров
}