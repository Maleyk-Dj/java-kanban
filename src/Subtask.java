import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;


public class Subtask extends TaskAbstract implements Serializable {
    private int epicId;

    public Subtask(String name, String description, int epicId, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.epicId = epicId;
        this.status = status;
    }

    public Subtask(String name, String description, TaskStatus status, int epicId, Duration duration, LocalDateTime starTime) {
        this(name, description, epicId, status);
        this.duration = duration;
        this.startTime = starTime;
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
