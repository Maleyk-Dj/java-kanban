import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest <T extends TaskManager> {

    protected T manager;

    protected abstract T createTaskManager();

    @BeforeEach
    void setUp() {
        manager = createTaskManager();
    }

    @Test
    void shouldAddAndGetTask() {
        Task task = new Task("Test", "Description", TaskStatus.NEW,Duration.ofMinutes(30),LocalDateTime.now() );
        int taskId = manager.addTask(task);
        Task saved = manager.getTask(taskId);

        assertNotNull(saved);
        assertEquals(taskId, saved.getId());
        assertEquals(task.getName(), saved.getName());
    }

    @Test
    void shouldAddAndGetEpicAndSubtask() {
        Epic epic = new Epic("Epic1", "Epic Desc",TaskStatus.DONE);
        int epicId = manager.addEpic(epic);

        Subtask subtask = new Subtask("Sub", "Sub Desc",TaskStatus.IN_PROGRESS,epicId, Duration.ofMinutes(15),LocalDateTime.now());
        subtask.setEpicId(epicId);
        int subtaskId = manager.addSubtask(subtask);

        Subtask savedSubtask = manager.getSubtask(subtaskId);
        Epic savedEpic = manager.getEpic(epicId);

        assertNotNull(savedSubtask);
        assertEquals(subtaskId, savedSubtask.getId());
        assertEquals(epicId, savedSubtask.getEpicId());

        List<Subtask> subtasks = manager.getEpicSubtasks(epicId);
        assertEquals(1, subtasks.size());
        assertEquals(subtaskId, subtasks.get(0).getId());
    }

    @Test
    void shouldUpdateTask() {
        Task task = new Task("Initial", "Initial Desc", TaskStatus.DONE, Duration.ofMinutes(10),LocalDateTime.now());
        int taskId = manager.addTask(task);

        Task updated = new Task("Updated", "Updated Desc",TaskStatus.NEW, Duration.ofMinutes(20),LocalDateTime.now().plusHours(1));
        updated.setId(taskId);
        manager.updateTask(updated);

        Task loaded = manager.getTask(taskId);
        assertEquals("Updated", loaded.getName());
        assertEquals("Updated Desc", loaded.getDescription());
    }

    @Test
    void shouldRemoveTasksAndSubtasksAndEpics() {
        Task task = new Task( "Task", "Task Desc",TaskStatus.IN_PROGRESS, Duration.ofMinutes(10),
                LocalDateTime.of(2025, 1, 1, 10, 0));
        int taskId = manager.addTask(task);

        Epic epic = new Epic("Epic", "Epic Desc",TaskStatus.IN_PROGRESS);
        int epicId = manager.addEpic(epic);

        Subtask subtask = new Subtask("Sub", "Sub Desc",TaskStatus.NEW,epicId, Duration.ofMinutes(10), LocalDateTime.of
                (2025, 1, 1, 11, 0)); // на час позже
        subtask.setEpicId(epicId);
        int subId = manager.addSubtask(subtask);

        manager.removeTask(taskId);
        manager.removeSubtask(subId);
        manager.removeEpic(epicId);

        assertNull(manager.getTask(taskId));
        assertNull(manager.getSubtask(subId));
        assertNull(manager.getEpic(epicId));
    }

    @Test
    void epicStatusShouldUpdateBasedOnSubtasks() {
        Epic epic = new Epic("Epic", "Desc",TaskStatus.NEW);
        int epicId = manager.addEpic(epic);

        Subtask sub1 = new Subtask("s1", "d",TaskStatus.NEW,epicId, Duration.ofMinutes(10), LocalDateTime.now());
        sub1.setEpicId(epicId);
        int id1 = manager.addSubtask(sub1);

        Subtask sub2 = new Subtask("s2", "d", TaskStatus.NEW,epicId, Duration.ofMinutes(10),LocalDateTime.now().plusMinutes(20));
        sub2.setEpicId(epicId);
        int id2 = manager.addSubtask(sub2);

        Epic updatedEpic = manager.getEpic(epicId);
        assertEquals(TaskStatus.NEW, updatedEpic.getStatus());

        sub1.setStatus(TaskStatus.DONE);
        sub2.setStatus(TaskStatus.DONE);
        manager.updateSubtask(sub1);
        manager.updateSubtask(sub2);

        updatedEpic = manager.getEpic(epicId);
        assertEquals(TaskStatus.DONE, updatedEpic.getStatus());
    }

    @Test
    void shouldReturnPrioritizedTasks() {
        Task task1 = new Task("t1", "d1",TaskStatus.DONE, Duration.ofMinutes(30), LocalDateTime.of(2024, 1, 1, 10, 0));
        Task task2 = new Task( "t2", "d2",TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, 1, 1, 8, 0));
        manager.addTask(task1);
        manager.addTask(task2);

        List<TaskAbstract> sorted = List.copyOf(manager.getPrioritizedTasks());
        assertEquals(task2.getName(), sorted.get(0).getName());
        assertEquals(task1.getName(), sorted.get(1).getName());
    }

    @Test
    void shouldThrowExceptionOnTimeIntersection() {
        Task task1 = new Task( "t1", "d1",TaskStatus.NEW, Duration.ofMinutes(60) ,LocalDateTime.of(2024, 1, 1, 10, 0));
        Task task2 = new Task( "t2", "d2", TaskStatus.NEW, Duration.ofMinutes(30),LocalDateTime.of(2024, 1, 1, 10, 30));
        manager.addTask(task1);
        assertThrows(ManagerSaveException.class, () -> manager.addTask(task2));
    }
}