package task;
import taskstatus.TaskStatus;

import java.util.Objects;

public class Task {

    public int id;
    public String name;
    public String description;
    public TaskStatus status;

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
}

