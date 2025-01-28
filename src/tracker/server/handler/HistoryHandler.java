package tracker.server.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import tracker.manager.TaskManager;
import tracker.server.adapter.DurationAdapter;
import tracker.server.adapter.LocalDateTimeAdapter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class HistoryHandler extends BaseHttpHandler {
    private final Gson gson;
    private final TaskManager taskManager;
    String response;

    public HistoryHandler(TaskManager newTaskManager) {
        this.taskManager = newTaskManager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = String.valueOf(exchange.getRequestURI());
        System.out.println("Обрабатывается запрос " + path + " с методом " + method);

        if (method.equals("GET")) {
            getHistoryList(exchange);
        } else {
            sendText(exchange, "Такой операции не существует", 405);
        }
    }

    private void getHistoryList(HttpExchange exchange) throws IOException {
            response = gson.toJson(taskManager.getHistory());
            sendText(exchange, response, 200);
        }
    }