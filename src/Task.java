import java.io.Serializable;
import java.util.Objects;
import java.io.Serializable;


public class Task extends TaskAbstract implements Serializable {

    private String name;
    private String description;
    private int id;
    private TaskStatus status;

    public Task(String name, String description, TaskStatus status) {
        super(TaskType.TASK, name, description, status);
    }

    @Override
    public String toCSV() {
        return String.format("%d,%s,%s,%s,%s,", getId(), getType(), getName(), getStatus(), getDescription());
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Task task = (Task) object;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", type=" + getType() +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }
}
