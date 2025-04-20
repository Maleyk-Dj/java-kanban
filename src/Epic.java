import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import java.util.Objects;

public class Epic extends TaskAbstract implements Serializable {

    private List<Integer> subtaskIds = new ArrayList<>();
    public LocalDateTime endTime;

    public Epic(TaskType type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskId(int subtaskId) {
        subtaskIds.add(subtaskId);
    }

    public void removeSubtaskId(int subtaskId) {
        subtaskIds.remove((Integer) subtaskId);
    }

    public void clearSubtaskIds() {
        subtaskIds.clear();
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) return null;
        return startTime.plus(duration);
    }


    @Override
    public String toCSV() {
        String startTimeStr = startTime != null ? startTime.toString() : "";
        String durationStr = duration != null ? String.valueOf(duration.toMinutes()) : "";
        return String.format("%d,%s,%s,%s,%s,%s,%s,",
                id, type, name, status, description, durationStr, startTimeStr);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Epic)) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskIds, epic.subtaskIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }

    public void setSubtaskIds(List<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}



