package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskTracker.manager.Managers;
import taskTracker.manager.TaskManager;
import taskTracker.tasks.Epic;
import taskTracker.tasks.Status;
import taskTracker.tasks.SubTask;
import taskTracker.tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {
    TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
    }

    //Test add
    @Test
    void addTaskTest() {
        Task task = new Task("Test addTask", "Test addTask description", Status.NEW);
        taskManager.addTask(task);
        assertEquals(1, taskManager.getTasks().size());
    }

    @Test
    void addEpicTest() {
        Epic epic = new Epic("Test addEpic", "Test addEpic description");
        taskManager.addEpic(epic);
        assertEquals(1, taskManager.getEpics().size());
    }

    @Test
    void addSubtaskTest() {
        Epic epic = new Epic("Test addEpic", "Test addEpic description");
        SubTask subTask = new SubTask("Test addSubtask", "Test addSubtask description", Status.NEW, epic.getId());
        taskManager.addSubtask(subTask);
        assertEquals(1, taskManager.getSubtasks().size());
    }

    //Test get

    @Test
    void getTaskTest() {
        Task task = new Task("Test addTask", "Test addTask description", Status.NEW);
        taskManager.addTask(task);
        assertEquals(1, taskManager.getTaskById(1).getId());
    }

    @Test
    void getEpicTest() {
        Epic epic = new Epic("Test addEpic", "Test addEpic description");
        taskManager.addEpic(epic);
        assertEquals(1, taskManager.getEpicById(1).getId());
    }

    @Test
    void getSubtaskTest() {
        Epic epic = new Epic("Test addEpic", "Test addEpic description");
        SubTask subTask = new SubTask("Test addSubtask", "Test addSubtask description", Status.NEW, epic.getId());
        taskManager.addSubtask(subTask);
        assertEquals(1, taskManager.getSubtaskById(1).getId());
    }

    //Test delete

    @Test
    void deleteTaskByIdTest() {
        Task task = new Task("Test addTask", "Test addTask description", Status.NEW);
        taskManager.addTask(task);
        taskManager.deleteTask(1);
        assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    void deleteAllTaskTest() {
        Task task = new Task("Test addTask", "Test addTask description", Status.NEW);
        taskManager.addTask(task);
        taskManager.deleteAllTasks();
        assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    void deleteEpicByIdTest() {
        Epic epic = new Epic("Test addEpic", "Test addEpic description");
        taskManager.addEpic(epic);
        taskManager.deleteEpic(1);
        assertEquals(0, taskManager.getEpics().size());
    }

    @Test
    void deleteEpicAllTest() {
        Epic epic = new Epic("Test addEpic", "Test addEpic description");
        taskManager.addEpic(epic);
        taskManager.deleteAllEpics();
        assertEquals(0, taskManager.getEpics().size());
    }

    @Test
    void deleteSubTaskByIdTest() {
        Epic epic = new Epic("Test addEpic", "Test addEpic description");
        SubTask subTask = new SubTask("Test addSubtask", "Test addSubtask description", Status.NEW, epic.getId());

        taskManager.addEpic(epic);
        taskManager.addSubtask(subTask);
        taskManager.deleteSubtask(2);

        assertEquals(0, taskManager.getSubtasks().size());
        assertEquals(0, taskManager.getEpicById(1).getSubtasksByEpic().size());
    }

    @Test
    void deleteAllSubTasksTest() {
        Epic epic = new Epic("Test addEpic", "Test addEpic description");
        SubTask subTask1 = new SubTask("Test addSubtask", "Test addSubtask description", Status.NEW, epic.getId());
        SubTask subTask2 = new SubTask("Test addSubtask", "Test addSubtask description", Status.NEW, epic.getId());

        taskManager.addEpic(epic);
        taskManager.addSubtask(subTask1);
        taskManager.addSubtask(subTask2);
        taskManager.deleteAllSubtasks();

        assertEquals(0, taskManager.getSubtasks().size());
        assertEquals(0, taskManager.getEpicById(1).getSubtasksByEpic().size());
    }

//Test update

    @Test
    void updateTaskTest() {
        Task task = new Task("Test addTask", "Test addTask description", Status.NEW);
        taskManager.addTask(task);
        Task updatedTask = taskManager.getTaskById(1);
        updatedTask.setDescription("Test: changed description");
        taskManager.updateTask(updatedTask);
        assertEquals("Test: changed description", taskManager.getTaskById(1).getDescription());
    }

    @Test
    void updateEpicTest() {
        Epic epic = new Epic("Test addEpic", "Test addEpic description");
        taskManager.addEpic(epic);
        Epic updatedEpic = taskManager.getEpicById(1);
        updatedEpic.setDescription("Test: changed description");
        taskManager.updateEpic(updatedEpic);
        assertEquals("Test: changed description", taskManager.getEpicById(1).getDescription());
    }


    @Test
    void updateSubTaskByIdTest() {
        Epic epic = new Epic("Test addEpic", "Test addEpic description");
        SubTask subTask = new SubTask("Test addSubtask", "Test addSubtask description", Status.NEW, 1);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subTask);
        SubTask updatedSubTask = (SubTask) taskManager.getSubtaskById(2);
        updatedSubTask.setStatus(Status.DONE);
        taskManager.updateSubtask(updatedSubTask);
        assertEquals(Status.DONE, taskManager.getSubtaskById(2).getStatus());
    }

    @Test
    void setIdAndGenerateIdNotConflictTest() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        final int taskId = taskManager.addTask(task).getId();
        taskManager.updateTask(new Task(task.getName(), task.getDescription(), task.getStatus(), taskId + 1));
        assertNull(taskManager.getTaskById(taskId + 1), "Задачи с заданным id и сгенерированным id конфликтуют внутри менеджера!");
    }

    @Test
    void historyVersionTest() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        final int taskId = taskManager.addTask(task).getId();
        taskManager.getTaskById(taskId);
        assertEquals(1, taskManager.getHistory().size(), "История просмотров не сохранена!");
        taskManager.updateTask(new Task(task.getName(), task.getDescription(), Status.IN_PROGRESS, task.getId()));
        taskManager.getTaskById(taskId);
        assertEquals(2, taskManager.getHistory().size(), "История просмотров не сохранена!");
        assertNotEquals(taskManager.getHistory().getFirst(), taskManager.getHistory().getLast(), "История не сохраняет предыдущую версию задачи!");
    }

    @Test
    void immutabilityTaskTest() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        final int taskId = taskManager.addTask(task).getId();
        Task taskRef = new Task(task.getName(), task.getDescription(), task.getStatus(), taskId);
        assertEquals(task, taskRef, "Задача поменялась при добавлении в taskManager");
    }
}
