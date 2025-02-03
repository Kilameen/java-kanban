package manager.serverTest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
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
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpTaskManagerTasksTest {
    private static final String TASK_URL = "http://localhost:8080/tasks";
    private static final String SUBTASK_URL = "http://localhost:8080/subtasks";
    private static final String EPIC_URL = "http://localhost:8080/epics";
    private static final String HISTORY_URL = "http://localhost:8080/history";
    private static final String PRIORITIZED_URL = "http://localhost:8080/prioritized";
    private static TaskManager taskManager;
    private static HttpTaskServer httpTaskServer;
    private static Gson gson;
    protected Task task;
    protected Epic epic;
    protected SubTask subtask;
    HttpClient client;

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = Managers.getDefault();
        httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        task = new Task("Task_1", Status.NEW, "Task_desc_1", LocalDateTime.of(2025, 1, 25, 9, 11), 15L);
        epic = new Epic("Epic_1", "Epic_desc_1");
        subtask = new SubTask("Subtask_1_1", Status.NEW, "Subtask_desc_1_1", LocalDateTime.of(2025, 1, 25, 13, 11), 15L, epic.getId());

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

    public void clearTaskManager() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
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
    public void addTaskToServer() throws IOException, InterruptedException {
        taskManager.addTask(task);
        postTask(URI.create(TASK_URL), task, client);
        URI url = URI.create(TASK_URL);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<Task> tasksFromManager = taskManager.getTasks();
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
    }

//test addToServer

    @Test
    public void addEpicToServer() throws IOException, InterruptedException {
        postTask(URI.create(EPIC_URL), epic, client);
        URI url = URI.create(EPIC_URL);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<Epic> epicsFromManager = taskManager.getEpics();
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void addSubtaskToServer() throws IOException, InterruptedException {
        postTask(URI.create(SUBTASK_URL), subtask, client);
        URI url = URI.create(SUBTASK_URL);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<SubTask> subtasksFromManager = taskManager.getSubtasks();
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
    }

    //testGetToServer

    @Test
    void getAllTasksAndTasksById() throws IOException, InterruptedException {
        taskManager.addTask(task);
        postTask(URI.create(TASK_URL), task, client);
        URI url = URI.create(TASK_URL + "/?id=" + task.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task task = gson.fromJson(response.body(), Task.class);
        assertEquals("Task_1", task.getName());
        List<Task> tasksFromManager = taskManager.getTasks();
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    void getAllEpicAndEpicsById() throws IOException, InterruptedException {
        taskManager.addEpic(epic);
        URI url = URI.create(EPIC_URL + "/?id=" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Epic epic = gson.fromJson(response.body(), Epic.class);
        assertEquals("Epic_1", epic.getName());
        List<Epic> epicsFromManager = taskManager.getEpics();
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
    }

    @Test
    void getAllSubtaskAndSubtasksById() throws IOException, InterruptedException {
        taskManager.addSubtask(subtask);
        URI url = URI.create(SUBTASK_URL + "/?id=" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        SubTask subtask = gson.fromJson(response.body(), SubTask.class);
        assertEquals("Subtask_1_1", subtask.getName());
        List<SubTask> subtasksFromManager = taskManager.getSubtasks();
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
    }

    //TestDeleteFromServer

    @Test
    void deleteTaskAllById() throws IOException, InterruptedException {
        taskManager.addTask(task);
        postTask(URI.create(TASK_URL), task, client);
        URI url = URI.create(TASK_URL + "/?id=" + task.getId());

        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("Задача удалена!", response.body());
    }

    @Test
    void deleteEpicAllById() throws IOException, InterruptedException {
        taskManager.addEpic(epic);
        postTask(URI.create(EPIC_URL), epic, client);
        URI url = URI.create(EPIC_URL + "/?id=" + epic.getId());

        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("Эпик удален!", response.body());
    }

    @Test
    void deleteSubtaskAllById() throws IOException, InterruptedException {
        Epic epic = new Epic(1, "Epic_1", Status.NEW, "Epic_desc_1", LocalDateTime.now().plusHours(1), 15L);
        taskManager.addEpic(epic); // Добавляем эпик в менеджер задач
        SubTask subtask = new SubTask(2, "Subtask_1_1", Status.NEW, "Subtask_desc_1_1", epic.getEndTime().plusHours(1), 15L, epic.getId());
        taskManager.addSubtask(subtask);

        postTask(URI.create(SUBTASK_URL), subtask, client);
        URI url = URI.create(SUBTASK_URL + "/?id=" + subtask.getId());

        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("Подзадача удалена!", response.body());
    }

    @Test
    void testDeleteTaskFromServer() throws IOException, InterruptedException {
        taskManager.addTask(task);
        postTask(URI.create(TASK_URL), task, client);
        URI url = URI.create(TASK_URL);

        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("Все задачи удалены!", response.body());
    }

    @Test
    void testDeleteEpicFromServer() throws IOException, InterruptedException {
        taskManager.addEpic(epic);
        postTask(URI.create(EPIC_URL), epic, client);
        URI url = URI.create(EPIC_URL);

        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("Все эпики и подзадачи удалены!", response.body());
    }

    @Test
    void TestDeleteSubtaskFromServer() throws IOException, InterruptedException {
        Epic epic = new Epic(1, "Epic_1", Status.NEW, "Epic_desc_1", LocalDateTime.now().plusHours(1), 15L);
        taskManager.addEpic(epic); // Добавляем эпик в менеджер задач
        SubTask subtask = new SubTask(2, "Subtask_1_1", Status.NEW, "Subtask_desc_1_1", epic.getEndTime().plusHours(1), 15L, epic.getId());
        taskManager.addSubtask(subtask);

        postTask(URI.create(SUBTASK_URL), subtask, client);
        URI url = URI.create(SUBTASK_URL);

        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("Все подзадачи удалены!", response.body());
    }

    //testHistoryToAndFormServer

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

    @Test
    void testGetHistoryWithSingleTask() throws IOException, InterruptedException {
        taskManager.addTask(task);
        taskManager.getTaskById(task.getId());

        URI url = URI.create(HISTORY_URL);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
        List<Task> history = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(1, history.size(), "История должна содержать одну задачу");
        assertEquals(task.getName(), history.get(0).getName(), "Имя задачи должно совпадать");
    }

    @Test
    void testGetHistoryWithSingleEpic() throws IOException, InterruptedException {
        taskManager.addEpic(epic);
        taskManager.getEpicById(epic.getId());

        URI url = URI.create(HISTORY_URL);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
        List<Task> history = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(1, history.size(), "История должна содержать одну задачу");
        assertEquals(epic.getName(), history.get(0).getName(), "Имя задачи должно совпадать");
    }

    @Test
    void testGetHistoryWithSingleSubtask() throws IOException, InterruptedException {
        taskManager.addSubtask(subtask);
        taskManager.getSubtaskById(subtask.getId());

        URI url = URI.create(HISTORY_URL);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
        List<Task> history = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(1, history.size(), "История должна содержать одну задачу");
        assertEquals(subtask.getName(), history.get(0).getName(), "Имя задачи должно совпадать");
    }

    @Test
    void testGetHistoryWithTwoTask() throws Exception {
        taskManager.addTask(task);
        Task task2 = new Task(2, "Имя задачи", Status.NEW, "Описание задачи", LocalDateTime.of(2025, 10, 11, 12, 0), 15L);

        taskManager.addTask(task2);
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task2.getId());

        URI url = URI.create(HISTORY_URL);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
        List<Task> history = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(2, history.size(), "История должна содержать одну задачу");
        assertEquals(task.getName(), history.get(0).getName(), "Имя задачи должно совпадать");
        assertEquals(task2.getName(), history.get(1).getName(), "Имя задачи должно совпадать");
    }

    @Test
    void testClearHistoryTask() throws Exception {
        taskManager.addTask(task);
        taskManager.getTaskById(task.getId());
        taskManager.deleteTask(task.getId());

        URI url = URI.create(HISTORY_URL);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ожидали код ответа 200");
        List<Task> history = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertTrue(history.isEmpty(), "Неверное количество задач");
    }

    @Test
    void testClearHistoryEpic() throws Exception {
        taskManager.addEpic(epic);
        taskManager.getEpicById(epic.getId());
        taskManager.deleteEpic(epic.getId());

        URI url = URI.create(HISTORY_URL);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ожидали код ответа 200");
        List<Task> history = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertTrue(history.isEmpty(), "Неверное количество задач");
    }

    @Test
    void testClearHistorySubtask() throws Exception {
        Epic epic = new Epic(1, "Epic_1", Status.NEW, "Epic_desc_1", LocalDateTime.now().plusHours(1), 15L);
        taskManager.addEpic(epic); // Добавляем эпик в менеджер задач
        SubTask subtask = new SubTask(2, "Subtask_1_1", Status.NEW, "Subtask_desc_1_1", epic.getEndTime().plusHours(1), 15L, epic.getId());
        taskManager.addSubtask(subtask);

        taskManager.getSubtaskById(subtask.getId());
        taskManager.deleteSubtask(subtask.getId());

        URI url = URI.create(HISTORY_URL);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ожидали код ответа 200");
        List<Task> history = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertTrue(history.isEmpty(), "Неверное количество задач");
    }

    //testPrioritizedToAndFromServer

    @Test
    public void testGetPrioritizedTasksWhenEmpty() throws IOException, InterruptedException {
        URI url = URI.create(PRIORITIZED_URL);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(0, tasks.size(), "Список приоритетных задач должен быть пустым");
    }

    @Test
    public void testGetPrioritizedTasksWithOneTask() throws IOException, InterruptedException {
        taskManager.addTask(task);
        taskManager.getTaskById(task.getId());

        URI url = URI.create(PRIORITIZED_URL);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
        List<Task> prioritized = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(1, prioritized.size(), "История должна содержать одну задачу");
        assertEquals(task.getName(), prioritized.get(0).getName(), "Имя задачи должно совпадать");
    }

    @Test
    void testGetPrioritizedTasksWithTwoTask() throws Exception {
        taskManager.addTask(task);
        Task task2 = new Task("Имя задачи", Status.NEW, "Описание задачи", LocalDateTime.of(2024, 1, 25, 12, 0), 15L);
        taskManager.addTask(task2);

        URI url = URI.create(PRIORITIZED_URL);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
        List<Task> prioritized = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(2, prioritized.size(), "Неверное количество задач");
        assertEquals(task2.getId(), prioritized.getFirst().getId(), "Задачи не расставлены по приоритету");
    }

    @Test
    void testDeletePrioritizedTask() throws Exception {
        taskManager.addTask(task);
        Task task2 = new Task(2, "Имя задачи", Status.NEW, "Описание задачи", LocalDateTime.of(2025, 1, 25, 12, 0), 15L);
        taskManager.addTask(task2);
        taskManager.deleteTask(task.getId());

        URI url = URI.create(PRIORITIZED_URL);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
        List<Task> prioritized = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(1, prioritized.size(), "Неверное количество задач");
    }

    //testUpdate

    @Test
    void testUpdateTask() throws Exception {
        taskManager.addTask(task);
        task.setStatus(Status.IN_PROGRESS);
        task.setName("Обновленное имя задачи");
        task.setStartTime(LocalDateTime.of(2025, 10, 23, 12, 0));
        String taskJson = gson.toJson(task);
        URI url = URI.create(TASK_URL + "/?id=" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
    }

    @Test
    void testUpdateEpic() throws Exception {
        taskManager.addEpic(epic);
        epic.setStatus(Status.IN_PROGRESS);
        epic.setName("Обновленное имя задачи");
        epic.setStartTime(LocalDateTime.of(2025, 10, 23, 12, 0));
        String taskJson = gson.toJson(epic);
        URI url = URI.create(EPIC_URL + "/?id=" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
    }

    @Test
    void testUpdateSubtask() throws Exception {
        Epic epic = new Epic(1, "Epic_1", Status.NEW, "Epic_desc_1", LocalDateTime.now().plusHours(1), 15L);
        taskManager.addEpic(epic); // Добавляем эпик в менеджер задач
        SubTask subtask = new SubTask(2, "Subtask_1_1", Status.NEW, "Subtask_desc_1_1", epic.getEndTime().plusHours(1), 15L, epic.getId());
        taskManager.addSubtask(subtask);
        taskManager.getSubtaskById(subtask.getId());
        subtask.setStatus(Status.IN_PROGRESS);
        subtask.setName("Обновленное имя задачи");
        subtask.setStartTime(LocalDateTime.of(2025, 10, 23, 12, 0));
        String taskJson = gson.toJson(subtask);
        URI url = URI.create(SUBTASK_URL + "/?id=" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
    }
}