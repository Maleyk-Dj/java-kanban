import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private List<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
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
    public String toString() {
        return "Epic{" + "name='" + getName() + '\'' + ", description='" + getDescription() + '\'' + ", id=" + getId() +
                ", status=" + getStatus() + "subtaskIds=" + subtaskIds + '}';
    }
}
