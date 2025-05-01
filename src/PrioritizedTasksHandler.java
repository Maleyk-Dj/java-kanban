import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class PrioritizedTasksHandler extends BaseHttpHandler implements HttpHandler {

    public PrioritizedTasksHandler(Gson gson, TaskManager taskManager) {
        super(gson, taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if ("GET".equals(exchange.getRequestMethod())) {
                String response = gson.toJson(taskManager.getPrioritizedTasks());
                sendText(exchange, response);
            } else {
                exchange.sendResponseHeaders(405, 0);
                exchange.close();
            }
        } catch (Exception e) {
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
            e.printStackTrace();
        }
    }
}
