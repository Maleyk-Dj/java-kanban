import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    public TaskHandler(Gson gson, TaskManager taskManager) {
        super(gson, taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET":
                    hadleGet(exchange);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange);
                    break;
                default:
                    exchange.sendResponseHeaders(405, 0);
                    exchange.close();
                    break;
            }
        } catch (Exception e) {
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
            e.printStackTrace();
        }
    }

    private void hadleGet(HttpExchange exchange) throws IOException {
        List<Task> tasks = taskManager.getTasks();
        String response = gson.toJson(tasks);
        sendText(exchange, response);
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(body, Task.class);

        try {
            if (task.getId() != 0 && (taskManager.getTask(task.getId()) != null)) {
                taskManager.updateTask(task);
            } else {
                taskManager.addTask(task);
            }
            exchange.sendResponseHeaders(200, 0);
        } catch (IllegalStateException e) {
            sendHasInteractions(exchange, "Пересение задач: " + e.getMessage());
        } finally {
            exchange.close();
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query != null && query.startsWith("id=")) {
            int id = Integer.parseInt(query.substring(3));
            Task task = taskManager.getTask(id);
            if (task != null) {
                taskManager.removeTask(id);
                sendText(exchange, "Task удалена");
            } else {
                sendNotFound(exchange, "Task c id=" + id + " не найдена");
            }
        } else {
            taskManager.removeAllTasks();
            sendText(exchange, "Все задачи удалены");
        }
    }
}
