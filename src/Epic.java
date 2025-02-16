import java.util.ArrayList;

public class Epic extends Task{

    private ArrayList <Integer> subtaskIds;

    public Epic(String name, String description,ArrayList<Integer> subtaskIds) {
        super(name, description);
        this.subtaskIds = subtaskIds;
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setSubtaskIds(ArrayList<Integer> subtaskIds) {
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
