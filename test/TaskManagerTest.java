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
        Task task = new Task(TaskType.TASK, "Test", "Description", LocalDateTime.now(), Duration.ofMinutes(30));
        int taskId = manager.addTask(task);
        Task saved = manager.getTask(taskId);

        assertNotNull(saved);
        assertEquals(taskId, saved.getId());
        assertEquals(task.getName(), saved.getName());
    }

    @Test
    void shouldAddAndGetEpicAndSubtask() {
        Epic epic = new Epic(TaskType.EPIC, "Epic1", "Epic Desc");
        int epicId = manager.addEpic(epic);

        Subtask subtask = new Subtask(TaskType.SUBTASK, "Sub", "Sub Desc", LocalDateTime.now(), Duration.ofMinutes(15));
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
        Task task = new Task(TaskType.TASK, "Initial", "Initial Desc", LocalDateTime.now(), Duration.ofMinutes(10));
        int taskId = manager.addTask(task);

        Task updated = new Task(TaskType.TASK, "Updated", "Updated Desc", LocalDateTime.now().plusHours(1), Duration.ofMinutes(20));
        updated.setId(taskId);
        manager.updateTask(updated);

        Task loaded = manager.getTask(taskId);
        assertEquals("Updated", loaded.getName());
        assertEquals("Updated Desc", loaded.getDescription());
    }

    @Test
    void shouldRemoveTasksAndSubtasksAndEpics() {
        Task task = new Task(TaskType.TASK, "Task", "Task Desc",
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(10));
        int taskId = manager.addTask(task);

        Epic epic = new Epic(TaskType.EPIC, "Epic", "Epic Desc");
        int epicId = manager.addEpic(epic);

        Subtask subtask = new Subtask(TaskType.SUBTASK, "Sub", "Sub Desc",
                LocalDateTime.of(2025, 1, 1, 11, 0), Duration.ofMinutes(10)); // на час позже
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
        Epic epic = new Epic(TaskType.EPIC, "Epic", "Desc");
        int epicId = manager.addEpic(epic);

        Subtask sub1 = new Subtask(TaskType.SUBTASK, "s1", "d", LocalDateTime.now(), Duration.ofMinutes(10));
        sub1.setEpicId(epicId);
        int id1 = manager.addSubtask(sub1);

        Subtask sub2 = new Subtask(TaskType.SUBTASK, "s2", "d", LocalDateTime.now().plusMinutes(20), Duration.ofMinutes(10));
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
        Task task1 = new Task(TaskType.TASK, "t1", "d1", LocalDateTime.of(2024, 1, 1, 10, 0), Duration.ofMinutes(30));
        Task task2 = new Task(TaskType.TASK, "t2", "d2", LocalDateTime.of(2024, 1, 1, 8, 0), Duration.ofMinutes(30));
        manager.addTask(task1);
        manager.addTask(task2);

        List<TaskAbstract> sorted = List.copyOf(manager.getPrioritizedTasks());
        assertEquals(task2.getName(), sorted.get(0).getName());
        assertEquals(task1.getName(), sorted.get(1).getName());
    }

    @Test
    void shouldThrowExceptionOnTimeIntersection() {
        Task task1 = new Task(TaskType.TASK, "t1", "d1", LocalDateTime.of(2024, 1, 1, 10, 0), Duration.ofMinutes(60));
        Task task2 = new Task(TaskType.TASK, "t2", "d2", LocalDateTime.of(2024, 1, 1, 10, 30), Duration.ofMinutes(30));
        manager.addTask(task1);
        assertThrows(ManagerSaveException.class, () -> manager.addTask(task2));
    }
}