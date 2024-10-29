package TaskTracker.tasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtaskIds;

    public Epic(String name, String description) {
        super(name, description,Status.NEW);
        subtaskIds = new ArrayList<>();
    }

    public List<Integer> getSubtasksByEpic() {
        return subtaskIds;
    }

    @Override
    public String toString() {
        return "model.Task.Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", subtaskIds=" + subtaskIds +
                '}';
    }

}