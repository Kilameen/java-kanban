package tracker.server.handler;

import com.sun.net.httpserver.HttpExchange;
import tracker.manager.TaskManager;
import tracker.server.adapter.DurationAdapter;
import tracker.server.adapter.LocalDateTimeAdapter;
import tracker.tasks.Epic;
import tracker.tasks.SubTask;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import tracker.tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static java.util.Objects.isNull;

public class EpicHandler extends BaseHttpHandler {
    private final Gson gson;
    private final TaskManager taskManager;
    String response;

    public EpicHandler(TaskManager newTaskManager) {
        this.taskManager = newTaskManager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                executeGETRequest(exchange);
                break;
            case "POST":
                executePOSTRequest(exchange);
                break;
            case "DELETE":
                executeDELETERequest(exchange);
                break;
            default:
                sendText(exchange, "Такой операции не существует", 405);
        }
    }

    private void executeGETRequest(HttpExchange exchange) throws IOException {
        if (exchange.getRequestURI().getQuery() == null) {
            response = gson.toJson(taskManager.getEpics());
            sendText(exchange, response, 200);
            return;
        }

        if (getTaskId(exchange).isEmpty()) {
            sendText(exchange, "Некорректный id " + getTaskId(exchange), 400);
            return;
        }

        int id = getTaskId(exchange).get();
        Epic epicById = taskManager.getEpicById(id);
        if (isNull(epicById)) {
            sendText(exchange, "Задач с id " + id + " не найдено!", 404);
            return;
        }
        response = gson.toJson(epicById);
        sendText(exchange, response, 200);
    }

    private void executePOSTRequest(HttpExchange exchange) throws IOException {
        try {
        InputStream json = exchange.getRequestBody();
        String jsonEpic = new String(json.readAllBytes(), DEFAULT_CHARSET);
        Epic epic = gson.fromJson(jsonEpic, Epic.class);
            if (epic == null) {
                sendText(exchange, "Задача не должна быть пустой!", 400);
                return;
            }
            Epic epicById = taskManager.getEpicById(epic.getId());
            if (epicById == null) {
                taskManager.addEpic(epic);
                sendText(exchange, "Задача добавлена!", 200);
                return;
            }
            taskManager.updateEpic(epic);
            sendText(exchange, "Задача обновлена", 201);

        } catch (JsonSyntaxException e) {
            sendText(exchange, "Получен некорректный JSON", 400);
        } catch (RuntimeException exp) {
            sendText(exchange, "Обнаружено пересечение по времени!", 406);
        }
    }

    private void executeDELETERequest(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) {
            sendText(exchange, "Не указан id эпика!", 404);
            return;
        }
        if (getTaskId(exchange).isEmpty()) {
            sendText(exchange, "Не указан id эпика!", 404);
            return;
        }
        int id = getTaskId(exchange).get();
        if (taskManager.getEpicById(id) == null) {
            sendText(exchange, "Эпиков с id " + id + " не найдено!", 404);
            return;
        }
        taskManager.deleteEpic(id);
        sendText(exchange, "Эпик удален!", 200);
    }
}