import java.util.Objects;

public abstract class TaskAbstract {

    protected int id;
    protected TaskType type;
    protected String name;
    protected String description;
    protected TaskStatus status;


    protected TaskAbstract(TaskType type, String name, String description, TaskStatus status) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TaskType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public abstract String toCSV();


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        TaskAbstract that = (TaskAbstract) object;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "TaskAbstract{" + "id=" + id + ", name='" + name + '\'' + ", type=" + type + ", description='" + description + '\'' + ", status=" + status + '}';
    }
}
