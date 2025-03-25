import java.io.Serializable;
import java.util.Objects;

public class Subtask extends TaskAbstract implements Serializable {
    private int epicId;

    public Subtask(String name, String description, TaskStatus status) {
        super(TaskType.SUBTASK, name, description, status);
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toCSV() {
        return String.format("%d,%s,%s,%s,%s,%d", getId(), getType(), getName(), getStatus(), getDescription(), epicId);
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

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        Subtask subtask = (Subtask) object;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}
