package task;

import taskstatus.TaskStatus;

import java.time.LocalDateTime;
import java.util.Objects;
import java.time.Duration;

public class Task {

    protected int id; // Изменено на protected для доступа в подклассах
    protected String name; // Изменено на protected
    protected String description; // Изменено на protected
    protected TaskStatus status; // Изменено на protected
    protected LocalDateTime startTime;
    protected Duration duration;

    public Task(int id, String name, String description, TaskStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    // Новый метод getType() для определения типа задачи
    public TaskType getType() {
        return TaskType.TASK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Task{id=%d, name='%s', description='%s', status=%s, duration=%s, startTime=%s}",
                id, name, description, status, duration, startTime);
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }
}

