import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class Epic extends TaskAbstract implements Serializable {

    private List<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String description, TaskStatus status) {
        super(TaskType.EPIC, name, description, status);
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setSubtaskIds(List<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

    public void addSubtaskId(int subtaskId) {
        this.subtaskIds.add(subtaskId);
    }

    @Override
    public String toCSV() {
        return String.format("%d,%s,%s,%s,%s,", getId(), getType(), getName(), getStatus(), getDescription());
    }

    @Override
    public String toString() {
        return "Epic{" + "name='" + getName() + '\'' + ", description='" + getDescription() + '\'' + ", id=" + getId() +
                ", status=" + getStatus() + "subtaskIds=" + subtaskIds + '}';
    }
}
