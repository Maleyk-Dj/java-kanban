import java.util.List;

public interface TaskManager {
    int addTask(Task task);

    void removeTask(int id);

    void removeAllTasks();

    Task getTask(int id);

    List<Task> getTasks();

    void updateTask(Task task);

    void updateEpic(Epic epic);

    int addSubtask(Subtask subtask);

    void removeSubtask(int id);

    void updateSubtask(Subtask subtask);

    Subtask getSubtask(int id);

    List<Subtask> getSubtasks();

    void removeAllSubtasks();

    List<Subtask> getEpicSubtasks(int epicId);

    int addEpic(Epic epic);

    void removeEpic(int id);

    Epic getEpic(int id);

    List<Epic> getAllEpics();

    void removeAllEpics();

    List<Task> getHistory();

}
