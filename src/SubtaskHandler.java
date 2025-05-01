import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    public SubtaskHandler(Gson gson, TaskManager taskManager) {
        super(gson, taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET":
                    handleGet(exchange);
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

    private void handleGet(HttpExchange exchange) throws IOException {
        List<Subtask> subtasks = taskManager.getSubtasks();
        sendText(exchange, gson.toJson(subtasks));
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Subtask subtask = gson.fromJson(body, Subtask.class);

        try {
            if (subtask.getId() != 0 && taskManager.getSubtask(subtask.getId()) != null) {
                taskManager.updateSubtask(subtask);
            } else {
                taskManager.addSubtask(subtask);
            }
            exchange.sendResponseHeaders(201, 0);
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
            if (taskManager.getSubtask(id) != null) {
                taskManager.removeSubtask(id);
                sendText(exchange, "Subtask удалена");
            } else {
                sendNotFound(exchange, "Subtask c id=" + id + " не найдена");
            }
        } else {
            taskManager.removeAllSubtasks();
            sendText(exchange, "Все подзадачи удалены");
        }
    }
}
