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
    void shouldReturnEmptyManagerWhenFileIsEmpty() throws IOException, ManagerSaveException {
        File emptyFile = File.createTempFile("empty", ".csv");
        Files.write(emptyFile.toPath(), List.of()); // пустой файл

        FileBackedTaskManager emptyManager = FileBackedTaskManager.loadFromFile(manager, emptyFile);
        assertTrue(emptyManager.getTasks().isEmpty());
        assertTrue(emptyManager.getSubtasks().isEmpty());
        assertTrue(emptyManager.getAllEpics().isEmpty());
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