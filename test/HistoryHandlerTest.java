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

class HistoryHandlerTest {

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();
    HttpClient client;


    public HistoryHandlerTest() throws IOException {

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
    public void testGetHistory() throws IOException, InterruptedException {

        Epic epic = new Epic("Epic", "Desc", TaskStatus.NEW);
        Task task = new Task("Task", "Desc", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now());

        manager.addEpic(epic);
        manager.addTask(task);

        //получаем задачи, чтобы они попали в историю
        manager.getEpic(epic.getId());
        manager.getTask(task.getId());

        // Шаг 3: отправляем GET-запрос на /history
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Шаг 4: проверка
        assertEquals(200, response.statusCode(), "Должен быть возвращён статус 200");

        Type taskListType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> history = gson.fromJson(response.body(), taskListType);

        assertNotNull(history, "История не должна быть null");
        assertEquals(2, history.size(), "История должна содержать две задачи");
        assertTrue(history.stream().anyMatch(t -> t.getName().equals("Epic")));
        assertTrue(history.stream().anyMatch(t -> t.getName().equals("Task")));

    }
}