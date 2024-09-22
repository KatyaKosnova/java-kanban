package task;

import taskstatus.TaskStatus;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private List<Subtask> subtasks;

    public Epic(int id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
        this.subtasks = new ArrayList<>();
    }

    // Метод для получения всех подзадач
    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    // Добавление подзадачи
    public void addSubtask(Subtask subtask) {
        if (subtask.getEpicId() == this.id) {
            throw new IllegalArgumentException("Эпик не может быть подзадачей самого себя.");
        }
        subtasks.add(subtask);
        updateEpicStatus();
    }



    // Удаление подзадачи
    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        updateEpicStatus();  // Обновляем статус эпика при удалении подзадачи
    }

    // Обновление подзадачи
    public void updateSubtask(Subtask subtask) {
        for (int i = 0; i < subtasks.size(); i++) {
            if (subtasks.get(i).getId() == subtask.getId()) {
                subtasks.set(i, subtask);
                updateEpicStatus();  // Обновляем статус эпика при изменении подзадачи
                break;
            }
        }
    }

    // Метод для обновления описания эпика
    public void setDescription(String description) {
        this.description = description;
    }

    // Метод для обновления статуса эпика в зависимости от статусов подзадач
    private void updateEpicStatus() {
        if (subtasks.isEmpty()) {
            this.status = TaskStatus.NEW;
            return;
        }

        boolean hasNew = false;
        boolean hasDone = false;

        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() == TaskStatus.NEW) {
                hasNew = true;
            } else if (subtask.getStatus() == TaskStatus.DONE) {
                hasDone = true;
            }
        }

        if (hasNew && !hasDone) {
            this.status = TaskStatus.IN_PROGRESS;
        } else if (hasDone && !hasNew) {
            this.status = TaskStatus.DONE;
        } else {
            this.status = TaskStatus.IN_PROGRESS; // Mixed statuses
        }
    }
}