import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskHandlerTest {

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();
    HttpClient client;

    public SubtaskHandlerTest() throws IOException {

    }

    @BeforeEach
    public void setUp() throws IOException {
        client = HttpClient.newHttpClient();
        manager.removeAllSubtasks();
        manager.removeAllTasks();
        manager.removeAllEpics();
        taskServer.start();
    }

    @AfterEach
    public void tearDown() {
        taskServer.stop();
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Desc", TaskStatus.IN_PROGRESS);
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Desc", TaskStatus.NEW, epic.getId(), Duration.ofMinutes(10), LocalDateTime.now());
        String json = gson.toJson(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Subtask> subtasks = manager.getSubtasks();
        assertEquals(1, subtasks.size());
        assertEquals("Subtask", subtasks.get(0).getName());
    }

    @Test
    public void testDeleteAllSubtasks() throws IOException, InterruptedException {
        Epic epic2 = new Epic("Epic 2", "Desc 2", TaskStatus.IN_PROGRESS);
        manager.addEpic(epic2);

        manager.addSubtask(new Subtask("Subtask 2", "Desc 2", TaskStatus.NEW, epic2.getId(), Duration.ofMinutes(25), LocalDateTime.now()));
        assertFalse(manager.getSubtasks().isEmpty(), "Подзадача не добавлена");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(manager.getSubtasks().isEmpty());
    }

    @Test
    public void testDeleteSubtaskDyId() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic 1", "Desc 1", TaskStatus.IN_PROGRESS);
        manager.addEpic(epic1);

        Subtask subtask = new Subtask("Subtask 3", "Desc 3", TaskStatus.NEW, epic1.getId(), Duration.ofMinutes(45), LocalDateTime.now());
        manager.addSubtask(subtask);
        int id = subtask.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks?id=" + id))
                .DELETE()
                .build();

        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertNull(manager.getSubtasks().stream()
                .filter(s -> s.getId() == id)
                .findFirst()
                .orElse(null), "Подзадача должна быть удалена");
    }

    @Test
    public void testDeleteSubtaskById_NotFound() throws IOException, InterruptedException {
        int notFoundId = 12345;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks?id=" + notFoundId))
                .DELETE()
                .build();

        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(500, response.statusCode());
    }
}