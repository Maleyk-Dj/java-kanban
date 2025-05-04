import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private HttpServer server;
    private final TaskManager taskManager;
    private static Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TaskHandler(gson, taskManager));
        server.createContext("/subtasks", new SubtaskHandler(gson, taskManager));
        server.createContext("/epics", new EpicHandler(gson, taskManager));
        server.createContext("/history", new HistoryHandler(gson, taskManager));
        server.createContext("/prioritized", new PrioritizedTasksHandler(gson, taskManager));
        server.start();
        System.out.println("HTTP-сервер запущен на порту " + PORT);
    }

    public void stop() {
        server.stop(0);
        System.out.println("HTTP-сервер остановлен");

    }

    public static void main(String[] args) {
        TaskManager manager = new InMemoryTaskManager();
        try {
            HttpTaskServer httpTaskServer = new HttpTaskServer(manager);
            httpTaskServer.start();
        } catch (IOException e) {
            System.out.println("Ошибка при запуске HTTP-сервера: " + e.getMessage());
        }
    }

    public static Gson getGson() {
        return gson;
    }
}