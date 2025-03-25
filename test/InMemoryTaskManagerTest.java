
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private TaskManager taskManager;
    private HistoryManager historyManager;

    @Test
    void addNewTask() {
        taskManager = new InMemoryTaskManager();
        Task task = new Task("Test addNewTask", "Test addNewTask description", TaskStatus.NEW);
        final int taskId = taskManager.addTask(task);
        final Task savedTask = taskManager.getTask(taskId);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");

    }

    @Test
    void taskEqualsById() {
        taskManager = new InMemoryTaskManager();
        Task task1 = new Task("Task 1", "Task description 1", TaskStatus.NEW);
        task1.setId(1);
        Task task2 = new Task("Task 2", "Task description 2", TaskStatus.NEW);
        task2.setId(1);
        assertEquals(task1, task2, "Задачи не совпадают");
    }

    @Test
    void subtaskEqualsById() {
        taskManager = new InMemoryTaskManager();
        Subtask subtask1 = new Subtask("Subtask 1", "Subtask description 1", TaskStatus.NEW);
        subtask1.setId(2);
        Subtask subtask2 = new Subtask("Subtask 2", "Subtask description 2", TaskStatus.DONE);
        subtask2.setId(2);
        assertEquals(subtask1, subtask2, "Задачи не совпадают");
    }

    @Test
    void epicEqualsById() {
        taskManager = new InMemoryTaskManager();
        Epic epic1 = new Epic("Epic 1", "Epic description 1", TaskStatus.NEW);
        epic1.setId(3);
        Epic epic2 = new Epic("Epic 2", "Epic description 2", TaskStatus.IN_PROGRESS);
        epic2.setId(3);
        assertEquals(epic1, epic2, "Задачи не совпадают");
    }

    @Test
    void epicCantBeSubtask() {
        taskManager = new InMemoryTaskManager();
        Epic epic1 = new Epic("Epic 1", "Epic description 1", TaskStatus.NEW);
        int epicId = taskManager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Subtask 1", "Subtask description 1", TaskStatus.NEW);
        taskManager.addSubtask(subtask1);
        Epic epic = taskManager.getEpic(epicId);

        assertNotNull(epic, "Пустое значение");

        List<Integer> subtaskIds = epic.getSubtaskIds();

        assertFalse(subtaskIds.contains(epicId), "Эпик входит сам в себя");

    }

    @Test
    void subtaskCantBeEpic() {
        taskManager = new InMemoryTaskManager();
        Epic epic1 = new Epic("Epic 1", "Epic description 1", TaskStatus.NEW);
        int epicId = taskManager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Subtask 1", "Subtask description 1", TaskStatus.NEW);
        subtask1.setId(epicId);
        taskManager.addSubtask(subtask1);
        Epic epic = taskManager.getEpic(epicId);

        assertNotNull(epic, "Пустое значение");

        List<Integer> subtaskIds = epic.getSubtaskIds();

        assertFalse(subtaskIds.contains(epicId), "Subtask превратился в свой эпик");
    }

    @Test
    void addTask_shouldAddTaskAndFindById() {
        taskManager = new InMemoryTaskManager();
        Task task = new Task("Test addNewTask", "Test addNewTask description", TaskStatus.NEW);
        int taskId = taskManager.addTask(task);
        Task testTask = taskManager.getTask(taskId);

        assertNotNull(testTask, "Задача не находится по указанному ID");
        assertEquals(task, testTask, "Задачи не совпадают");

    }

    @Test
    void addSubtask_shouldAddSubtaskAndFindById() {
        taskManager = new InMemoryTaskManager();
        Epic epic1 = new Epic("Epic 1", "Epic description 1", TaskStatus.NEW);
        int epicId = taskManager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Subtask 1", "Subtask description 1", TaskStatus.NEW);
        int subtaskId = taskManager.addSubtask(subtask1);
        Subtask testSubtask = taskManager.getSubtask(subtaskId);

        assertNotNull(testSubtask, "Подзадача не находится по указанному ID");
        assertEquals(subtask1, testSubtask, "Задачи не совпадают");
    }

    @Test
    void addEpic_shouldAddEpicAndFindById() {
        taskManager = new InMemoryTaskManager();
        Epic epic1 = new Epic("Epic 1", "Epic description 1", TaskStatus.NEW);
        int epicId = taskManager.addEpic(epic1);
        Epic testEpic = taskManager.getEpic(epicId);
        assertNotNull(testEpic, "Эпик не находится по указанному ID");
        assertEquals(epic1, testEpic, "Задачи не совпадают");

    }

    @Test
    void getTask_shouldReturnNullIfTaskNotFound() {
        taskManager = new InMemoryTaskManager();
        Task task = taskManager.getTask(23);
        assertNull(task, "Задача не найдена, возвращается null");
    }

    @Test
    void getSubtask_shouldReturnNullIfSubtaskNotFound() {
        taskManager = new InMemoryTaskManager();
        Subtask subtask = taskManager.getSubtask(23);
        assertNull(subtask, "Подзадача не найдена, возвращается null");
    }

    @Test
    void getEpic_shouldReturnNullIfEpicNotFound() {
        taskManager = new InMemoryTaskManager();
        Epic epic = taskManager.getEpic(23);
        assertNull(epic, "Эпик не найден, возвращается null");
    }

    @Test
    void getHistory() {
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager();
        Task task1 = new Task("Task 1", "Task description 1", TaskStatus.NEW);
        Epic epic1 = new Epic("Epic 1", "Epic description 1", TaskStatus.NEW);
        Task task2 = new Task("Task 1", "Task description 1", TaskStatus.NEW);
        Epic epic2 = new Epic("Epic 1", "Epic description 1", TaskStatus.NEW);
        Subtask subtask = new Subtask("Subtask 1", "Subtask description 1", TaskStatus.NEW);
        int subtaskId = taskManager.addSubtask(subtask);
        int taskId1 = taskManager.addTask(task1);
        int epicId1 = taskManager.addEpic(epic1);
        int taskId2 = taskManager.addTask(task2);
        int epicId2 = taskManager.addEpic(epic2);

        taskManager.getTask(taskId1);
        taskManager.getEpic(epicId1);
        taskManager.getTask(taskId2);
        taskManager.getEpic(epicId2);
        taskManager.getSubtask(subtaskId);

        List<Task> viewHistory = taskManager.getHistory();

        assertNotNull(viewHistory);
        assertEquals(5, viewHistory.size(), "История должна содержать 4 задачи");

    }

    @Test
    void shouldReturnHistoryContainsPreviousVersionOfTask() {
        taskManager = new InMemoryTaskManager();
        Task task = new Task("Test addNewTask", "Test addNewTask description", TaskStatus.NEW);
        int taskId = taskManager.addTask(task);
        Task testTask = taskManager.getTask(taskId);

        List<Task> history = taskManager.getHistory();

        testTask.setName("New Name");
        testTask.setDescription("New Description");
        testTask.setStatus(TaskStatus.IN_PROGRESS);

        assertEquals(1, history.size(), "История должна содержать одну задачу");

        Task taskHistory = history.get(0);

        assertEquals("Test addNewTask", taskHistory.getName(), "Имена задач совпадают");
        assertEquals("Test addNewTask description", taskHistory.getDescription(), "Описание задач не должны совпадать");

    }

    @Test
    void shouldReturnImmutableAfterAddingToManager() {
        taskManager = new InMemoryTaskManager();
        Task task = new Task("Test addNewTask", "Test addNewTask description", TaskStatus.NEW);
        int taskId = taskManager.addTask(task);

        Task taskCopy = taskManager.getTask(taskId);

        assertNotNull(taskCopy, "Задача не найдена");
        assertEquals("Test addNewTask", taskCopy.getName(), "Имена задач несовпадают");
        assertEquals("Test addNewTask description", taskCopy.getDescription(), "Описание задач не совпадают");
        assertEquals(TaskStatus.NEW, taskCopy.getStatus(), "Статусы задач не совпадают");
    }

    @Test
    void removeTask() {
        taskManager = new InMemoryTaskManager();
        Task task1 = new Task("Task 1", "Task description 1", TaskStatus.NEW);
        int taskId = taskManager.addTask(task1);
        taskManager.removeTask(taskId);
        List<Task> tasks = taskManager.getTasks();

        assertEquals(0, tasks.size(), "Задача не удалилась");
    }

}