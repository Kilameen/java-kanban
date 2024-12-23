package tracker.tasks;

public class SubTask extends Task {
    private final int epicID;

    public SubTask(String name, String description, Status status, int epicID) {
        super(name, description, status);
        this.epicID = epicID;
    }

    public SubTask(int id, String name, String description, Status status, int epicID) {
        super(id, name, description, status);
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
        return "model.Subtask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", epicID=" + epicID +
                '}';
    }
}