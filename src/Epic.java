import java.util.ArrayList;
import java.util.List;

public class Epic extends Task{

    private List<Integer> subtaskIds;

    public Epic(String name, String description,List<Integer> subtaskIds) {
        super(name, description);
        this.subtaskIds = subtaskIds;
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setSubtaskIds(List<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                "subtaskIds=" + subtaskIds +
                '}';
    }


}
