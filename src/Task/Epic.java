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
        if (subtask.getEpicId() != this.id) {
            throw new IllegalArgumentException("Подзадача должна принадлежать эпика.");
        }
        subtasks.add(subtask);
        updateEpicStatus();
    }


    // Удаление подзадачи
    public void removeSubtask(Subtask subtask) {
        if (subtask.getEpicId() != this.id) {
            throw new IllegalArgumentException("Подзадача должна принадлежать эпика.");
        }
        subtasks.remove(subtask);
        updateEpicStatus();  // Обновляем статус эпика при удалении подзадачи
    }

    // Обновление подзадачи
    public void updateSubtask(Subtask subtask) {
        if (subtask.getEpicId() != this.id) {
            throw new IllegalArgumentException("Подзадача должна принадлежать эпика.");
        }
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

        int newCount = 0;
        int doneCount = 0;

        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() == TaskStatus.NEW) {
                newCount++;
            } else if (subtask.getStatus() == TaskStatus.DONE) {
                doneCount++;
            }
        }

        if (newCount > 0 && doneCount == 0) {
            this.status = TaskStatus.IN_PROGRESS;
        } else if (doneCount == subtasks.size()) {
            this.status = TaskStatus.DONE;
        } else {
            this.status = TaskStatus.IN_PROGRESS; // Mixed statuses
        }
    }
}