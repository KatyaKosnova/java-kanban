package task;

import exception.InvalidSubtaskException;
import taskstatus.TaskStatus;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    // Поля для хранения подзадач и свойств эпика
    public List<Subtask> subtasks;
    public Duration duration; // Расчетная продолжительность эпика
    public LocalDateTime startTime; // Расчетная дата начала эпика
    public LocalDateTime endTime; // Расчетная дата окончания эпика
    public List<Subtask> prioritizedTasks; // Объявление списка приоритетных задач

    public Epic(int id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
        this.subtasks = new ArrayList<>();
        this.prioritizedTasks = new ArrayList<>(); // Инициализация списка приоритетных задач
        this.duration = Duration.ZERO; // Инициализация продолжительности
        this.startTime = null; // Инициализация времени начала
        this.endTime = null; // Инициализация времени окончания
    }

    // Метод для получения всех подзадач
    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    // Удаление подзадачи
    public void removeSubtask(Subtask subtask) {
        if (subtask.getEpicId() != this.getId()) {
            throw new IllegalArgumentException("Подзадача должна принадлежать эпика.");
        }
        subtasks.remove(subtask);
        updateEpicProperties(); // Обновляем свойства эпика при удалении подзадачи
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

    // Обновление статуса эпика
    public void updateEpicStatus() {
        if (subtasks.isEmpty()) {
            this.status = TaskStatus.NEW; // или любой другой статус по умолчанию
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() != TaskStatus.NEW) {
                allNew = false;
            }
            if (subtask.getStatus() != TaskStatus.DONE) {
                allDone = false;
            }
        }

        if (allNew) {
            this.status = TaskStatus.NEW;
        } else if (allDone) {
            this.status = TaskStatus.DONE;
        } else {
            this.status = TaskStatus.IN_PROGRESS;
        }
    }

    // Обновление свойств эпика на основе подзадач
    public void updateEpicProperties() {
        this.duration = Duration.ZERO; // Сбросить продолжительность
        this.startTime = null; // Сбросить дату начала
        this.endTime = null; // Сбросить дату окончания

        if (subtasks.isEmpty()) {
            this.status = TaskStatus.NEW;
            return;
        }

        LocalDateTime earliestStart = null;
        LocalDateTime latestEnd = null;

        for (Subtask subtask : subtasks) {
            this.duration = this.duration.plus(subtask.getDuration()); // Суммируем продолжительности

            if (earliestStart == null || (subtask.getStartTime() != null && subtask.getStartTime().isBefore(earliestStart))) {
                earliestStart = subtask.getStartTime(); // Находим самую раннюю дату начала
            }

            LocalDateTime subtaskEndTime = subtask.getEndTime();
            if (latestEnd == null || (subtaskEndTime != null && subtaskEndTime.isAfter(latestEnd))) {
                latestEnd = subtaskEndTime; // Находим самую позднюю дату окончания
            }
        }

        this.startTime = earliestStart; // Устанавливаем расчетное время начала
        this.endTime = latestEnd; // Устанавливаем расчетное время окончания
        updateEpicStatus(); // Обновляем статус эпика
    }

    // Геттеры для новых полей
    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    // Оверрайд метода toString()
    @Override
    public String toString() {
        return String.format("Epic{id=%d, name='%s', description='%s', status=%s, duration=%s, startTime=%s, endTime=%s}",
                id, name, description, status, duration, startTime, endTime);
    }

    @Override
    public TaskStatus getStatus() {
        return status; // Упрощено, поскольку статус уже обновляется в updateEpicStatus
    }

    public void addSubtask(Subtask subtask) throws InvalidSubtaskException {

        if (subtask.getEpicId() != this.getId()) {
            throw new InvalidSubtaskException("Подзадача должна принадлежать эпика."); // Изменено
        }

        subtasks.add(subtask); // Используем add вместо put
    }
}