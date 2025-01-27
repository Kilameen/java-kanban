package tracker.server.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import tracker.manager.TaskManager;
import tracker.server.adapter.DurationAdapter;
import tracker.server.adapter.LocalDateTimeAdapter;
import tracker.tasks.SubTask;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import static java.util.Objects.isNull;

public class SubtaskHandler extends BaseHttpHandler {
    private final Gson gson;
    private final TaskManager taskManager;
    String response;

    public SubtaskHandler(TaskManager newTaskManager) {
        this.taskManager = newTaskManager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
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
                sendText(exchange, "Такой операции не существует", 404);
        }
    }

    private void executeGETRequest(HttpExchange exchange) throws IOException {
        if (exchange.getRequestURI().getQuery() == null) {
            response = gson.toJson(taskManager.getSubtasks());
            sendText(exchange, response, 200);
            return;
        }
        if (getTaskId(exchange).isEmpty()) {
            sendText(exchange, "Некорректный id " + getTaskId(exchange), 400);
            return;
        }
        int id = getTaskId(exchange).get();
        SubTask subtaskById = (SubTask) taskManager.getSubtaskById(id);
        if (isNull(subtaskById)) {
            sendText(exchange, "Подзадач с id " + id + " не найдено!", 404);
            return;
        }
        response = gson.toJson(subtaskById);
        sendText(exchange, response, 200);
    }

    private void executePOSTRequest(HttpExchange exchange) throws IOException {
        try {
            InputStream json = exchange.getRequestBody();
            String jsonTask = new String(json.readAllBytes(), DEFAULT_CHARSET);
            SubTask subTask = gson.fromJson(jsonTask, SubTask.class);
            if (subTask == null) {
                sendText(exchange, "Подзадача не должна быть пустой!", 400);
                return;
            }
            SubTask subtaskById = (SubTask) taskManager.getSubtaskById(subTask.getId());
            if (subtaskById == null) {
                taskManager.addSubtask(subTask);
                sendText(exchange, "Подзадача добавлена!", 201);
                return;
            }
            taskManager.updateSubtask(subTask);
            sendText(exchange, "Подзадача обновлена!", 200);

        } catch (JsonSyntaxException e) {
            sendText(exchange, "Получен некорректный JSON", 400);
        } catch (RuntimeException exp) {
            sendText(exchange, "Обнаружено пересечение по времени ", 406);
        }
    }

    private void executeDELETERequest(HttpExchange exchange) throws IOException {
        if (exchange.getRequestURI().getQuery() == null) {
            sendText(exchange, "Не указан id подзадачи!", 404);
            return;
        }
        if (getTaskId(exchange).isEmpty()) {
            sendText(exchange, "Некорректный id подзадачи!", 404);
            return;
        }
        int id = getTaskId(exchange).get();
        if (taskManager.getSubtaskById(id) == null) {
            sendText(exchange, "Подзадачи с id " + id + " не найдено!", 404);
            return;
        }
        taskManager.deleteSubtask(id);
        sendText(exchange, "Подзадача удалена!", 200);
    }
}