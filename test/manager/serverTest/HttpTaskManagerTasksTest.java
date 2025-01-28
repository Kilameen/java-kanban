package manager.serverTest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.manager.HistoryManager;
import tracker.manager.Managers;
import tracker.manager.TaskManager;
import tracker.server.HttpTaskServer;
import tracker.server.adapter.DurationAdapter;
import tracker.server.adapter.LocalDateTimeAdapter;
import tracker.tasks.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerTasksTest {
    private static final String TASK_URL = "http://localhost:8080/tasks";
    private static final String SUBTASK_URL = "http://localhost:8080/subtasks";
    private static final String EPIC_URL = "http://localhost:8080/epics";
    private static final String HISTORY_URL = "http://localhost:8080/history";
    private static final String PRIORITIZED_URL = "http://localhost:8080/prioritized";
    private static TaskManager taskManager;
    private static HttpTaskServer httpTaskServer;
    private static HistoryManager historyManager;
    private static Gson gson;
    protected Task task;
    protected Epic epic;
    protected SubTask subtask;
    HttpClient client;

    @BeforeEach
    public void setUp() throws IOException, InterruptedException {
        taskManager = Managers.getDefault();
        httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        task = new Task(1, "Task_1", Status.NEW, "Task_desc_1", LocalDateTime.now(), 15L);
        epic = new Epic(2, "Epic_1", Status.NEW, "Epic_desc_1", task.getEndTime().plusHours(1), 15L);
        subtask = new SubTask(3, "Subtask_1_1", Status.NEW, "Subtask_desc_1_1", epic.getEndTime().plusHours(1), 15L, epic.getId());

        client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        System.out.println("Клиент создан");
    }

    @AfterEach
    public void shutDown() {
        clearTaskManager();
        httpTaskServer.stop();
    }

    @Test
    public void addTaskToServer() throws IOException, InterruptedException {
        taskManager.addTask(task);
        URI url = URI.create(TASK_URL);
        assertEquals(200, postTask(url, task, client).statusCode(), "Задача добавлена");
        List<Task> tasksFromManager = taskManager.getTasks();
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void addEpicToServer() throws IOException, InterruptedException {
        taskManager.addEpic(epic);
        URI url = URI.create(EPIC_URL);
        assertEquals(200, postTask(url, epic, client).statusCode(), "Задача добавлена");
        List<Epic> epicsFromManager = taskManager.getEpics();
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void addSubtaskToServer() throws IOException, InterruptedException {
        taskManager.addSubtask(subtask);
        URI url = URI.create(SUBTASK_URL);
        assertEquals(200, postTask(url, subtask, client).statusCode(), "Задача добавлена");
        List<SubTask> subtasksFromManager = taskManager.getSubtasks();
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    void getAllTasksAndTasksById() throws IOException, InterruptedException {
        taskManager.addTask(task);
        postTask(URI.create(TASK_URL), task, client);
        // Запрос задачи по ID
        URI url = URI.create(TASK_URL + "/?id=" +  task.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверка статуса и данных задачи
        assertEquals(200, response.statusCode());
        Task task = gson.fromJson(response.body(), Task.class);
        assertEquals("Task_1", task.getName());
    }

    @Test
    void getAllEpicAndEpicsById() throws IOException, InterruptedException {
        taskManager.addEpic(epic);
        postTask(URI.create(EPIC_URL), epic, client);
        URI url = URI.create(EPIC_URL + "/?id="+ epic.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Epic epic = gson.fromJson(response.body(), Epic.class);
        assertEquals("Epic_1", epic.getName());
    }

    @Test
    void getAllSubtaskAndSubtasksById() throws IOException, InterruptedException {
        taskManager.addSubtask(subtask);
        postTask(URI.create(SUBTASK_URL), subtask, client);
        URI url = URI.create(SUBTASK_URL + "/?id=" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        SubTask subtask = gson.fromJson(response.body(), SubTask.class);
        assertEquals("Subtask_1_1", subtask.getName());

    }

    @Test
    void deleteTaskAllById() throws IOException, InterruptedException {
        taskManager.addTask(task);
        postTask(URI.create(TASK_URL), task, client);
        URI url = URI.create(TASK_URL + "/?id=" + task.getId());

        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("Задача удалена!", response.body());
    }

    @Test
    void deleteEpicAllById() throws IOException, InterruptedException {
        taskManager.addEpic(epic);
        postTask(URI.create(EPIC_URL), epic, client);
        URI url = URI.create(EPIC_URL + "/?id=" + epic.getId());

        HttpRequest request3 = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals("Эпик удален!", response3.body());
    }

    @Test
    void deleteSubtaskAllById() throws IOException, InterruptedException {
        taskManager.addSubtask(subtask);
        postTask(URI.create(SUBTASK_URL), subtask, client);
        URI url = URI.create(SUBTASK_URL + "/?id=" + subtask.getId());

        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("Подзадача удалена!", response.body());
    }

    public void clearTaskManager() {
        taskManager.deleteAll();
    }

    public HttpResponse<String> postTask(URI url, Task task,
                                         HttpClient client) throws IOException, InterruptedException {

        String json = gson.toJson(task);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void testGetHistoryWhenEmpty() throws IOException, InterruptedException {
        URI url = URI.create(HISTORY_URL);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
        List<Task> history = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(0, history.size(), "История должна быть пустой");
    }

//    @Test
//    void testGetHistoryWithSingleTask() throws IOException, InterruptedException {
//        taskManager.addTask(task);
//        // Добавление задачи в TaskManager
//        taskManager.getTaskById(task.getId());
//        // Задача добавляется в историю
//
//        URI url = URI.create(HISTORY_URL);
//        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
//        List<Task> history = gson.fromJson(response.body(), new TypeToken<List<Task>>() {}.getType());
//        assertEquals(1, history.size(), "История должна содержать одну задачу");
//        assertEquals(task.getName(), history.get(0).getName(), "Имя задачи должно совпадать");
//    }
    @Test
    public void testGetPrioritizedTasksWhenEmpty() throws IOException, InterruptedException {
        URI url = URI.create(PRIORITIZED_URL);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {}.getType());
        assertEquals(0, tasks.size(), "Список приоритетных задач должен быть пустым");
    }

//    @Test
//    public void testGetPrioritizedTasksWithOneTask() throws IOException, InterruptedException {
//        taskManager.addTask(task); // Добавление задачи в TaskManager
//
//        URI url = URI.create(PRIORITIZED_URL);
//        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
//        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {}.getType());
//        assertEquals(1, tasks.size(), "Список приоритетных задач должен содержать одну задачу");
//        assertEquals("Task_1", tasks.get(0).getName(), "Имя задачи должно совпадать");
//    }
    }