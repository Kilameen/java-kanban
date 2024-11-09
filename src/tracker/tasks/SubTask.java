package tracker.tasks;

public class SubTask extends Task {
    private final int epicID;

    public SubTask(String name, String description, Status status, int epicID) {
        super(name, description, status);
        this.epicID = epicID;
    }


    public int getEpicID() {
        return epicID;
    }
    @Override
    public void setId(int id) {
        if (epicID == id) {
            throw new IllegalArgumentException("SubTaskId can't be equal to epicId.");
        }
        super.setId(id);
    }
    @Override
    public String toString() {
        return "model.Subtask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", epicID=" + epicID +
                '}';
    }
}