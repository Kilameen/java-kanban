package tracker.server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import tracker.manager.TaskManager;
import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {
    private final Gson gson;
    private final TaskManager taskManager;
    String response;

    public HistoryHandler(TaskManager newTaskManager) {
        this.taskManager = newTaskManager;
        this.gson = new Gson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if (method.equals("GET")) {
            getHistoryList(exchange);
        } else {
            sendText(exchange, "Такой операции не существует", 404);
        }
    }

    private void getHistoryList(HttpExchange exchange) throws IOException {
        if (taskManager.getHistory().isEmpty()) {
            sendText(exchange, "История пуста!", 200);
        } else {
            response = gson.toJson(taskManager.getHistory());
            sendText(exchange, response, 200);
        }
    }
}