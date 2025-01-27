package tracker.server.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import tracker.server.adapter.DurationAdapter;
import tracker.server.adapter.LocalDateTimeAdapter;
import tracker.manager.TaskManager;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class PrioritizedTasksHandler extends BaseHttpHandler {
    private final Gson gson;
    private final TaskManager taskManager;
    String response;

    public PrioritizedTasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        if (method.equals("GET")) {
            getPrioritizedTasks(exchange);
        } else {
            sendText(exchange, "Такой операции не существует", 404);
        }
    }

    private void getPrioritizedTasks(HttpExchange exchange) throws IOException {
        response = gson.toJson(taskManager.getPrioritizedTasks());
        sendText(exchange, response, 200);
    }
}