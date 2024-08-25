package TaskManager;
import Task.Task;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int HISTORY_LIMIT = 10;
    private final List<Task> history;

    public InMemoryHistoryManager() {
        this.history = new LinkedList<>();
    }

    @Override
    public void add(Task task) {
        if (history.size() == HISTORY_LIMIT) {
            history.remove(0);  // удаляем самую старую запись, если достигнут лимит
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
