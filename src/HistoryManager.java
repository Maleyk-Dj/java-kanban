import java.util.List;

public interface HistoryManager {

    void addToHistory(TaskAbstract task);

    void remove(int id);

    List<Task> getHistory();

}
