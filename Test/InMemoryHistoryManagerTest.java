import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;
    private TaskManager taskManager;
    private Task task;

    @Test
    void addToHistory() {
        historyManager = new InMemoryHistoryManager();
        historyManager.addToHistory(task);
        final List <Task> history = historyManager.getHistory();
        assertNotNull(history,"История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");

    }



}