package manager.serverTest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.manager.Managers;
import tracker.manager.TaskManager;
import tracker.server.HttpTaskServer;
import tracker.server.adapter.DurationAdapter;
import tracker.server.adapter.LocalDateTimeAdapter;
import tracker.tasks.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerTasksTest {
    private static final String TASK_URL = "http://localhost:8080/tasks";
    private static final String SUBTASK_URL = "http://localhost:8080/subtasks";
    private static final String EPIC_URL = "http://localhost:8080/epics";
    private static TaskManager taskManager;
    private static HttpTaskServer httpTaskServer;
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
        task = new Task(1,"Task_1", Status.NEW, "Task_desc_1", LocalDateTime.now(), 15L);

        epic = new Epic(2,"Epic_1", Status.NEW, "Epic_desc_1", task.getEndTime().plusHours(1), 15L);

        subtask = new SubTask(3,"Subtask_1_1", Status.NEW, "Subtask_desc_1_1", epic.getEndTime().plusHours(1), 15L, epic.getId());

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
    public void addTasksToTaskServer() throws IOException, InterruptedException {
        URI url = URI.create(TASK_URL);

        assertEquals(201, postTask(url, task, client).statusCode(), "Задача добавлена");

        url = URI.create(EPIC_URL);

        assertEquals(201, postTask(url, epic, client).statusCode(), "Задача добавлена");


        url = URI.create(SUBTASK_URL);

        assertEquals(201, postTask(url, subtask, client).statusCode(), "Задача добавлена");
    }

    @Test
    void getAllTasksAndTasksById() throws IOException, InterruptedException {

        postTask(URI.create(TASK_URL), task, client);
        URI url = URI.create(TASK_URL + "?id=1");

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task task = gson.fromJson(response.body(), Task.class);
        assertEquals("Task_1", task.getName());

        postTask(URI.create(EPIC_URL), epic, client);
        url = URI.create(EPIC_URL + "?id=2");

        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Epic epic = gson.fromJson(response.body(), Epic.class);
        assertEquals("Epic_1", epic.getName());

        postTask(URI.create(SUBTASK_URL), addSubtaskEpic(), client);
        url = URI.create(SUBTASK_URL + "?id=3");

        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        SubTask subtask = gson.fromJson(response.body(), SubTask.class);
        assertEquals("Subtask_1_1", subtask.getName());
    }

    @Test
    void deleteTaskAllById() throws IOException, InterruptedException {

        postTask(URI.create(TASK_URL), task, client);
        postTask(URI.create(EPIC_URL), epic, client);
        postTask(URI.create(SUBTASK_URL), addSubtaskEpic(), client);

        URI url = URI.create(SUBTASK_URL + "?id=3");

        HttpRequest request1 = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals("Подзадача удалена!", response1.body());

        url = URI.create(TASK_URL + "?id=1");

        request1 = HttpRequest.newBuilder().uri(url).DELETE().build();
        response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals("Задача удалена!", response1.body());

        url = URI.create(EPIC_URL + "?id=2");

        HttpRequest request3 = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals("Эпик удален!", response3.body());
    }

    public SubTask addSubtaskEpic() throws IOException, InterruptedException {
        subtask.setEpicID(1);
        return subtask;
    }

    public void clearTaskManager() {
       taskManager.deleteAll();
    }

    public HttpResponse<String> postTask(URI url, Task task,
                                         HttpClient client) throws IOException, InterruptedException {
        try {
            String json = gson.toJson(task);
            HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(body)
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        } catch (InterruptedException exception) {
            System.out.println(exception.getMessage());
        }
        return null;
    }
}