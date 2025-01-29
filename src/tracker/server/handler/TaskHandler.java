package tracker.server.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import tracker.manager.TaskManager;
import tracker.server.adapter.DurationAdapter;
import tracker.server.adapter.LocalDateTimeAdapter;
import tracker.tasks.Task;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import static java.util.Objects.isNull;

public class TaskHandler extends BaseHttpHandler {
    private final Gson gson;
    private final TaskManager taskManager;
    String response;

    public TaskHandler(TaskManager newTaskManager) {
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
            response = gson.toJson(taskManager.getTasks());
            sendText(exchange, response, 200);
            return;
        }
        if (getTaskId(exchange).isEmpty()) {
            sendText(exchange, "Некорректный id " + getTaskId(exchange), 400);
            return;
        }
        int id = getTaskId(exchange).get();
        Task taskById = taskManager.getTaskById(id);
        if (isNull(taskById)) {
            sendText(exchange, "Задач с id " + id + " не найдено!", 404);
            return;
        }
        response = gson.toJson(taskById);
        sendText(exchange, response, 200);
    }

    private void executePOSTRequest(HttpExchange exchange) throws IOException {
        try {
            InputStream json = exchange.getRequestBody();
            String jsonTask = new String(json.readAllBytes(), DEFAULT_CHARSET);
            Task task = gson.fromJson(jsonTask, Task.class);
            if (task == null) {
                sendText(exchange, "Задача не должна быть пустой!", 400);
                return;
            }
            Task taskById = taskManager.getTaskById(task.getId());
            if (taskById == null) {
                taskManager.addTask(task);
                sendText(exchange, "Задача добавлена!", 200);
                return;
            }
            taskManager.updateTask(task);
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
            // Удалить все задачи, если параметр не указан
            taskManager.deleteAllTasks(); // Метод для удаления всех задач
            sendText(exchange, "Все задачи удалены!", 200);
            return;
        }

        if (getTaskId(exchange).isEmpty()) {
            sendText(exchange, "Не указан id задачи ", 404);
            return;
        }
        int id = getTaskId(exchange).get();
        if (taskManager.getTaskById(id) == null) {
            sendText(exchange, "Задач с таким id " + id + " не найдено!", 404);
            return;
        }
        taskManager.deleteTask(id);
        sendText(exchange, "Задача удалена!", 200);
    }
}