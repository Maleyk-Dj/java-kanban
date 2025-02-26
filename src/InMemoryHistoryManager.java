import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List <Task> viewHistory = new ArrayList<>(10);

    @Override
    public void addToHistory(Task task) {
        if (viewHistory.size() >= 10) {
            viewHistory.remove(0);
        }
        viewHistory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(viewHistory);
    }
}
