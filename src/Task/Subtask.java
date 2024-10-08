package task;

import taskstatus.TaskStatus;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {

    public int epicId; // Идентификатор эпика, к которому принадлежит подзадача
    public Duration duration; // Продолжительность подзадачи
    public LocalDateTime startTime; // Время начала выполнения подзадачи
    public LocalDateTime endTime; // Время окончания подзадачи

    // Конструктор класса Subtask
    public Subtask(int id, String name, String description, TaskStatus status, int epicId,
                   Duration duration, LocalDateTime startTime) {
        super(id, name, description, status);
        this.epicId = epicId;
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = calculateEndTime(); // Вычисляем время окончания
    }

    public int getEpicId() {
        return epicId;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    // Вычисление времени окончания на основе времени начала и продолжительности
    private LocalDateTime calculateEndTime() {
        return startTime != null && duration != null ? startTime.plus(duration) : null;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subtask subtask)) return false;
        return epicId == subtask.epicId && super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return String.format("Subtask{id=%d, name='%s', description='%s', status=%s, epicId=%d, duration=%s, startTime=%s, endTime=%s}",
                id, name, description, status, epicId, duration, startTime, endTime);
    }
}