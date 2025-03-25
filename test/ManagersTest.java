import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void getDefault() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        TaskManager taskManager = Managers.getDefault();

        assertNotNull(taskManager, "taskmanager равен null");

        assertTrue(taskManager instanceof InMemoryTaskManager, "taskmanager не является экземпляром класса " +
                "InMemoryTaskManager");
        assertTrue(taskManager.getTasks().isEmpty(), "Список задач при инициализации не пуст");
        assertTrue(taskManager.getAllEpics().isEmpty(), "Список эпиков при инициализации не пуст");
        assertTrue(taskManager.getSubtasks().isEmpty(), "Список подзадач при инициализации не пуст");
        assertEquals(0, historyManager.getHistory().size(), "История просмотров при инициализации не пустая");

    }

    @Test
    void historyManager() {
        HistoryManager historyManager = Managers.historyManager();

        assertNotNull(historyManager, "historyManager равен null");
        assertTrue(historyManager instanceof InMemoryHistoryManager, "historyManager не является экземпляром класса " +
                "InMemoryTaskManager");
        assertEquals(0, historyManager.getHistory().size(), "История просмотров при инициализации не пустая");
    }
}