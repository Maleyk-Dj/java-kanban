import java.util.*;

public class

InMemoryHistoryManager implements HistoryManager {

    private final List <Task> viewHistory = new ArrayList<>();

    @Override
    public void addToHistory(Task task) {
        if (viewHistory.size() >= 10) {
            viewHistory.remove(0);
        }
        viewHistory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(viewHistory);}

}
