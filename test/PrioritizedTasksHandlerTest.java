import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PrioritizedTasksHandlerTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();
    HttpClient client;


    public PrioritizedTasksHandlerTest() throws IOException {

    }

    @BeforeEach
    public void setUp() throws IOException {
        client = HttpClient.newHttpClient();
        manager.removeAllTasks();
        manager.removeAllSubtasks();
        manager.removeAllEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {
        // Добавим две задачи с разным временем начала
        Task task1 = new Task("Task 1", "Desc 1", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2023, 1, 1, 12, 0));
        Task task2 = new Task("Task 2", "Desc 2", TaskStatus.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2023, 1, 1, 11, 0));

        manager.addTask(task1);
        manager.addTask(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type taskListType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> prioritized = gson.fromJson(response.body(), taskListType);

        assertNotNull(prioritized);
        assertEquals(2, prioritized.size());
        assertEquals("Task 2", prioritized.get(0).getName()); // раньше по времени
        assertEquals("Task 1", prioritized.get(1).getName());
    }

    @Test
    public void testInvalidMethodReturns405() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(405, response.statusCode());
    }
}