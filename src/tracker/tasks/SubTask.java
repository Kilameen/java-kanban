package tracker.tasks;

import java.time.LocalDateTime;

public class SubTask extends Task {
    private final int epicID;

    public SubTask(String name, String description, Status status, int epicID) {
        super(name, description, status);
        this.epicID = epicID;
    }

    public SubTask(String name, Status status, String description, LocalDateTime startTime, Long durationMinutes, int epicID) {
        super(name, status, description, startTime, durationMinutes);
        this.epicID = epicID;
    }

    public Type getType() {
        return Type.SUBTASK;
    }

    public int getEpicID() {
        return epicID;
    }

    @Override
    public void setId(int id) {
        if (epicID == id) {
            throw new IllegalArgumentException("Subtask id не может быть равен epicID.");
        }
        super.setId(id);
    }

    @Override
    public String toString() {
        String start = "";
        if (getStartTime() != null) {
            start = getStartTime().format(dateTimeFormatter);
        }

        return "model.Subtask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", startTime=" + start +
                ", duration=" + duration.toMinutes() +
                ", epicID=" + epicID +
                '}';
    }
}