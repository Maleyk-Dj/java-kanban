import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;


public class Subtask extends TaskAbstract implements Serializable {
    private int epicId;

    public Subtask(TaskType type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public Subtask(TaskType type, String name, String description, LocalDateTime starTime, Duration duration) {
        this(type, name, description);
        this.startTime = starTime;
        this.duration = duration;
    }

    @Override
    public String toCSV() {
        String startTimeStr = startTime != null ? startTime.toString() : "";
        String durationStr = duration != null ? duration.toString() : "";
        return String.format("%d,%s,%s,%s,%s,%d,%s,%s", id, type, name, status, description, epicId, durationStr, startTimeStr);
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}
