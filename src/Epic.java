import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtaskIds;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        subtaskIds = new ArrayList<>();
    }

    public List<Integer> getSubtasksByEpic() {
        return subtaskIds;
    }

    @Override
    public String toString() {
        return "model.Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", subtaskIds=" + subtaskIds +
                '}';
    }

    public void updateStatus(TaskManager taskManager) {
        List<SubTask> subtasks = taskManager.getSubtasksByEpicId(this.getId());
        if (subtasks.isEmpty()) {
            this.setStatus(Status.NEW);
            return;
        }

        boolean allDone = true;
        boolean anyInProgress = false;

        for (SubTask subtask : subtasks) {
            if (subtask.getStatus() == Status.IN_PROGRESS) {
                anyInProgress = true;
                break;
            }
            if (subtask.getStatus() != Status.DONE) {
                allDone = false;
            }
        }

        if (allDone) {
            this.setStatus(Status.DONE);
        } else if (anyInProgress) {
            this.setStatus(Status.IN_PROGRESS);
        } else {
            this.setStatus(Status.NEW);
        }
    }
}