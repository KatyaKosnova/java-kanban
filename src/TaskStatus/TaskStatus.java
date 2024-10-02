package taskstatus;

public enum TaskStatus {

    NEW,
    IN_PROGRESS,
    DONE;

    @Override
    public String toString() {
        return name(); // или любое другое строковое представление
    }
}
