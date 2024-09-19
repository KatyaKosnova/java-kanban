package TaskManager;

import Task.Task;
import java.util.List;

public interface HistoryManager {
    void add(Task task);          // Добавление задачи в историю
    void remove(int id);          // Удаление задачи по ID
    List<Task> getHistory();      // Получение истории просмотров
}