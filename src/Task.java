import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

public class Task extends TaskAbstract implements Serializable {

    public Task(String name, String description,TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status=status;
    }

    public Task(String name,String description,TaskStatus status,Duration duration,LocalDateTime startTime) {
        this(name,description,status);
        this.duration=duration;
        this.startTime=startTime;
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