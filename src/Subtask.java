public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    public int getEpicId() {
        return epicId;
    }


    @Override
    public boolean isSubtask() {
        return true;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                "epicId=" + epicId +
                '}';
    }
}
