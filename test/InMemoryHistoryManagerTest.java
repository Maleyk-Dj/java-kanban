import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private InMemoryHistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void beforeEach() {
        historyManager = new InMemoryHistoryManager();
        task1 = new Task(TaskType.TASK, "Task description 1", "Description");
        task1.setId(1);
        task2 = new Task(TaskType.TASK, "Task description 2", "Description2");
        task2.setId(2);
        task3 = new Task(TaskType.TASK, "Task description 3", "Description3");
        task3.setId(3);
    }

    @Test
    void addToHistory_shouldAddTaskToHistory() {
        historyManager.addToHistory(task1);
        List<Task> history = historyManager.getHistory();
        assertNotNull(history);
        assertEquals(1, history.size());
        assertEquals(task1.getId(), history.get(0).getId());
    }

    @Test
    void addToHistory_shouldAddCopyOfTaskToHistory() {
        historyManager.addToHistory(task1);
        List<Task> history = historyManager.getHistory();
        Task taskFromHistory = history.get(0);
        taskFromHistory.setName("Задача2");
        assertNotEquals(task1.getName(), taskFromHistory.getName());

    }

    @Test
    void addToHistory_shouldNotAddNullTask() {
        historyManager.addToHistory(null);
        List<Task> history = historyManager.getHistory();
        assertNotNull(history);
        assertTrue(history.isEmpty());
    }

    @Test
    void addToHistory_shouldMoveTheRepeatedTaskToTheEnd() {
        historyManager.addToHistory(task1);
        historyManager.addToHistory(task2);
        historyManager.addToHistory(task1);

        List<Task> history = historyManager.getHistory();

        assertEquals(task1.getId(), history.get(1).getId());
        assertEquals(2, history.size());
    }

    @Test
    void remove_shouldRemoveTaskToHistiry() {
        historyManager.addToHistory(task2);
        historyManager.addToHistory(task3);
        historyManager.remove(task2.getId());
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
    }

    @Test
    void getHistory_shouldReturnTheCorrectOrder() {
        historyManager.addToHistory(task3);
        historyManager.addToHistory(task1);
        historyManager.addToHistory(task2);
        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size());
        assertEquals(task3.getId(), history.get(0).getId());
        assertEquals(task1.getId(), history.get(1).getId());
        assertEquals(task2.getId(), history.get(2).getId());
    }

}
