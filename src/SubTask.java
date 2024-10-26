public class SubTask extends Task {
    private final int epicID;

    public SubTask(String name, String description, Status status, int idEpic) {
        super(name, description, status);
        this.epicID = idEpic;
    }

    public int getEpicID() {
        return epicID;
    }

    @Override
    public String toString() {
        return "model.Subtask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", epicID" + epicID +
                '}';
    }
}