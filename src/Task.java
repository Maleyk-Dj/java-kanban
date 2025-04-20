import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.io.Serializable;


public class Task extends TaskAbstract implements Serializable {

    public Task(TaskType type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public Task(TaskType type, String name, String description, LocalDateTime startTime, Duration duration) {
        this(type, name, description);
        this.startTime = startTime;
        this.duration = duration;
    }

    @Override
    public String toCSV() {
        String startTimeStr = startTime != null ? startTime.toString() : "";
        String durationStr = duration != null ? String.valueOf(duration.toMinutes()) : "";
        return String.format("%d,%s,%s,%s,%s,,%s,%s,", id, type, name, status, description, durationStr, startTimeStr);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }
}