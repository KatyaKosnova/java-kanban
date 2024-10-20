package task;

import taskstatus.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private int epicId;
    private Duration duration; // Поле для длительности подзадачи
    private LocalDateTime startTime; // Поле для времени начала подзадачи

    public Subtask(int id, String name, String description, TaskStatus status, int epicId, Duration duration, LocalDateTime startTime) {
        super(id, name, description, status);
        this.epicId = epicId;
        this.duration = duration; // Инициализация длительности
        this.startTime = startTime; // Инициализация времени начала
    }

    public int getEpicId() {
        return epicId;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }
}
