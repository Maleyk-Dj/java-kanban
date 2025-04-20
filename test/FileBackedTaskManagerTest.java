import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private File file;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(file);
    }

    @BeforeEach
    void setUp() {
        try {
            file = File.createTempFile("taskmanager", ".csv");
            manager = new FileBackedTaskManager(file);
        } catch (IOException e) {
            fail("Не удалось создать временный файл: " + e.getMessage());
        }
    }

    @Test
    void shouldSaveAndLoadTaskWithAllFields() throws ManagerSaveException {
        Task task = new Task(TaskType.TASK, "Test Task", "Desc",
                LocalDateTime.of(2025, 4, 20, 12, 0), Duration.ofMinutes(60));
        int taskId = manager.addTask(task);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(manager, file);
        Task loadedTask = loaded.getTask(taskId);

        assertNotNull(loadedTask);
        assertEquals(task.getName(), loadedTask.getName());
        assertEquals(task.getDescription(), loadedTask.getDescription());
        assertEquals(task.getDuration(), loadedTask.getDuration());
        assertEquals(task.getStartTime(), loadedTask.getStartTime());
    }

    @Test
    void shouldSaveAndLoadSubtaskAndEpicRelationship() throws ManagerSaveException {
        Epic epic = new Epic(TaskType.EPIC, "Epic", "With Subtasks");
        int epicId = manager.addEpic(epic);

        Subtask subtask = new Subtask(TaskType.SUBTASK, "Sub", "Details",
                LocalDateTime.of(2025, 4, 21, 10, 0), Duration.ofMinutes(30));
        subtask.setEpicId(epicId);
        int subtaskId = manager.addSubtask(subtask);

        // Сохраняем данные в файл
        manager.save();

        // Загружаем данные из файла
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(manager, file);

        Epic loadedEpic = loaded.getEpic(epicId);
        Subtask loadedSubtask = loaded.getSubtask(subtaskId);

        assertNotNull(loadedEpic);
        assertEquals(1, loadedEpic.getSubtaskIds().size());
        assertTrue(loadedEpic.getSubtaskIds().contains(subtaskId));
        assertNotNull(loadedSubtask);
        assertEquals(epicId, loadedSubtask.getEpicId());
    }

    @Test
    void shouldReturnEmptyManagerWhenFileIsEmpty() throws IOException, ManagerSaveException {
        File emptyFile = File.createTempFile("empty", ".csv");
        Files.write(emptyFile.toPath(), List.of()); // пустой файл

        FileBackedTaskManager emptyManager = FileBackedTaskManager.loadFromFile(manager, emptyFile);
        assertTrue(emptyManager.getTasks().isEmpty());
        assertTrue(emptyManager.getSubtasks().isEmpty());
        assertTrue(emptyManager.getAllEpics().isEmpty());
    }

    @Test
    void shouldThrowExceptionForInvalidLineFormat() throws IOException {
        File badFile = File.createTempFile("bad", ".csv");
        Files.write(badFile.toPath(), List.of("id,type,name", "1,TASK,TooShort"));

        assertThrows(ManagerSaveException.class, () -> {
            FileBackedTaskManager.loadFromFile(manager, badFile);
        });
    }

    @Test
    void saveShouldCreateCorrectCSVFormat() throws IOException {
        Task task = new Task(TaskType.TASK, "Test Save", "CSV Format",
                LocalDateTime.of(2025, 4, 22, 15, 0), Duration.ofMinutes(45));
        manager.addTask(task);

        List<String> lines = Files.readAllLines(file.toPath());

        assertFalse(lines.isEmpty());
        assertEquals("id,type,name,status,description,duration,startTime,epic", lines.get(0));
        assertTrue(lines.get(1).contains("Test Save"));
        assertTrue(lines.get(1).contains("2025-04-22T15:00"));
    }
}