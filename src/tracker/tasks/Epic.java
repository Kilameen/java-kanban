package tracker.tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    public final List<Integer> subtaskIds = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, Status status, String description, LocalDateTime startTime, Long durationMinutes) {
        super(name, status, description, startTime, durationMinutes);
    }

    public Epic(int id, String name, Status status, String description, LocalDateTime startTime, Long durationMinutes) {
        super(id, name, status, description, startTime, durationMinutes);
    }

    public List<Integer> getSubtasksByEpic() {
        return subtaskIds;
    }

    public void addSubTaskId(int subTaskId) {
        if (subTaskId != this.id) {
            this.subtaskIds.add(subTaskId);
        }
    }

    public Type getType() {
        return Type.EPIC;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        String start = "";
        if (getStartTime() != null) {
            start = getStartTime().format(dateTimeFormatter);
        }

        return "model.Task.Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", startTime=" + start +
                ", duration=" + duration.toMinutes() +
                ", subtaskIds=" + subtaskIds +
                '}';
    }
}