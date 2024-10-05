package taskmanager;

import java.io.File;

public class Managers {

    // Возвращает стандартный менеджер задач
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    // Возвращает стандартный менеджер истории
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    // Возвращает менеджер задач, который сохраняет данные в файл
    public static FileBackedTaskManager getFileBackedManager(File file) {
        return new FileBackedTaskManager(file);
    }
}