package task;
import taskstatus.TaskStatus;

public class Subtask extends Task {

    private int epicId;


    public Subtask(int id, String name, String description, TaskStatus status, int epicId) {

        super(id, name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {

        return epicId;
    }
}
