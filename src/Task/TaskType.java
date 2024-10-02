package task;

public enum TaskType {
    TASK,
    EPIC,
    SUBTASK;

    @Override
    public String toString() {
        return name(); // или любое другое строковое представление
    }
}
