import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    public EpicHandler(Gson gson, TaskManager taskManager) {
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
        List<Epic> epics = taskManager.getAllEpics();
        sendText(exchange, gson.toJson(epics));
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(body, Epic.class);

        if (epic.getId() != 0 && taskManager.getEpic(epic.getId()) != null) {
            taskManager.updateEpic(epic);
        } else {
            taskManager.addEpic(epic);
        }
        exchange.sendResponseHeaders(201, 0);
        exchange.close();
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();

        if (query != null && query.startsWith("id=")) {
            int id = Integer.parseInt(query.substring(3));
            if (taskManager.getEpic(id) != null) {
                taskManager.removeEpic(id);
                sendText(exchange, "Epic удален");
            } else {
                sendNotFound(exchange, "Epic c id=" + id + " не найден");
            }
        } else {
            taskManager.removeAllEpics();
            sendText(exchange, "Все задачи удалены");
        }
    }
}
