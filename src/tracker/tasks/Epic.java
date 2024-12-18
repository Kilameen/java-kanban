package tracker.tasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    public final List<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description,Status.NEW);

    }


    public Epic(int id, String name, String description, Status status) {
        super(name, description,status);
        setId(id);
    }

    public List<Integer> getSubtasksByEpic() {
        return subtaskIds;
    }

    public void addSubTaskId(int subTaskId) {
        if (subTaskId != this.id) {
            this.subtaskIds.add(subTaskId);
        }
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