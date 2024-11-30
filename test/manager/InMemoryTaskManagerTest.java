package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.manager.Managers;
import tracker.manager.TaskManager;
import tracker.tasks.Epic;
import tracker.tasks.Status;
import tracker.tasks.SubTask;
import tracker.tasks.Task;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {
    TaskManager taskManager;

    protected String TASK_NAME_TEXT = "Test addTask";
    protected String TASK_DESCRIPTION_TEXT = "Test addTask description";
    protected String EPIC_NAME_TEXT = "Test addEpic";
    protected String EPIC_DESCRIPTION_TEXT = "Test addEpic description";
    protected String SUBTASK_NAME_TEXT = "Test addSubtask";
    protected String SUBTASK_DESCRIPTION_TEXT = "Test addSubtask description";

    Task task1 = new Task(TASK_NAME_TEXT, TASK_DESCRIPTION_TEXT, Status.NEW);
    Task task2 = new Task(TASK_NAME_TEXT, TASK_DESCRIPTION_TEXT, Status.NEW);
    Task task3 = new Task(TASK_NAME_TEXT, TASK_DESCRIPTION_TEXT, Status.NEW);

    Epic epic1 = new Epic(EPIC_NAME_TEXT, EPIC_DESCRIPTION_TEXT);
    Epic epic2 = new Epic(EPIC_NAME_TEXT, EPIC_DESCRIPTION_TEXT);
    Epic epic3 = new Epic(EPIC_NAME_TEXT, EPIC_DESCRIPTION_TEXT);

    SubTask subTask1 = new SubTask(SUBTASK_NAME_TEXT, SUBTASK_DESCRIPTION_TEXT, Status.NEW, epic1.getId());
    SubTask subTask2 = new SubTask(SUBTASK_NAME_TEXT, SUBTASK_DESCRIPTION_TEXT, Status.NEW, epic1.getId());
    SubTask subTask3 = new SubTask(SUBTASK_NAME_TEXT, SUBTASK_DESCRIPTION_TEXT, Status.NEW, epic1.getId());

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
    }

    //Test add
    @Test
    void addTaskTest() {
        taskManager.addTask(task1);
        assertEquals(1, taskManager.getTasks().size());
    }

    @Test
    void addEpicTest() {
        taskManager.addEpic(epic1);
        assertEquals(1, taskManager.getEpics().size());
    }

    @Test
    void addSubtaskTest() {
        taskManager.addSubtask(subTask1);
        assertEquals(1, taskManager.getSubtasks().size());
    }

    @Test
    public void addNewTaskTest() {
        final int taskId = taskManager.addTask(task1).getId();
        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task1, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task1, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void addNewEpicTest() {
        final int epicId = taskManager.addEpic(epic1).getId();
        final Epic savedEpic = taskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic1, savedEpic, "Задачи не совпадают.");

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic1, epics.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void addNewSubtaskTest() {
        final int epicId = taskManager.addEpic(epic1).getId();
        SubTask subtask = new SubTask(SUBTASK_NAME_TEXT, SUBTASK_DESCRIPTION_TEXT, Status.NEW, 1);
        final int subtaskId = taskManager.addSubtask(subtask).getId();
        final SubTask savedSubtask = (SubTask) taskManager.getSubtaskById(subtaskId);

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(subtask, savedSubtask, "Задачи не совпадают.");

        final List<SubTask> subTasks = taskManager.getSubtasks();

        assertNotNull(subTasks, "Задачи не возвращаются.");
        assertEquals(1, subTasks.size(), "Неверное количество задач.");
        assertEquals(subtask, subTasks.getFirst(), "Задачи не совпадают.");
    }

    //Test get

    @Test
    void getTaskTest() {
        taskManager.addTask(task1);
        assertEquals(1, taskManager.getTaskById(1).getId());
    }

    @Test
    void getEpicTest() {
        taskManager.addEpic(epic1);
        assertEquals(1, taskManager.getEpicById(1).getId());
    }

    @Test
    void getSubtaskTest() {
        taskManager.addSubtask(subTask1);
        assertEquals(1, taskManager.getSubtaskById(1).getId());
    }

    //Test delete

    @Test
    void deleteTaskByIdTest() {
        taskManager.addTask(task1);
        taskManager.deleteTask(1);
        assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    void deleteAllTaskTest() {
        taskManager.addTask(task1);
        taskManager.deleteAllTasks();
        assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    void deleteEpicByIdTest() {
        taskManager.addEpic(epic1);
        taskManager.deleteEpic(1);
        assertEquals(0, taskManager.getEpics().size());
    }

    @Test
    void deleteEpicAllTest() {
        taskManager.addEpic(epic1);
        taskManager.deleteAllEpics();
        assertEquals(0, taskManager.getEpics().size());
    }

    @Test
    void deleteSubTaskByIdTest() {
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subTask1);
        taskManager.deleteSubtask(2);

        assertEquals(0, taskManager.getSubtasks().size());
        assertEquals(0, taskManager.getEpicById(1).getSubtasksByEpic().size());
    }

    @Test
    void deleteAllSubTasksTest() {
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subTask1);
        taskManager.addSubtask(subTask2);
        taskManager.deleteAllSubtasks();

        assertEquals(0, taskManager.getSubtasks().size());
        assertEquals(0, taskManager.getEpicById(1).getSubtasksByEpic().size());
    }

//Test update

    @Test
    void updateTaskTest() {
        taskManager.addTask(task1);
        Task updatedTask = taskManager.getTaskById(1);
        updatedTask.setDescription("Test: changed description");
        taskManager.updateTask(updatedTask);
        assertEquals("Test: changed description", taskManager.getTaskById(1).getDescription());
    }

    @Test
    void updateEpicTest() {
        taskManager.addEpic(epic1);
        Epic updatedEpic = taskManager.getEpicById(1);
        updatedEpic.setDescription("Test: changed description");
        taskManager.updateEpic(updatedEpic);
        assertEquals("Test: changed description", taskManager.getEpicById(1).getDescription());
    }

    @Test
    void updateSubTaskByIdTest() {
        SubTask subTask = new SubTask(SUBTASK_NAME_TEXT, SUBTASK_DESCRIPTION_TEXT, Status.NEW, 1);
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subTask);
        SubTask updatedSubTask = (SubTask) taskManager.getSubtaskById(2);
        updatedSubTask.setStatus(Status.DONE);
        taskManager.updateSubtask(updatedSubTask);
        assertEquals(Status.DONE, taskManager.getSubtaskById(2).getStatus());
    }

//Check Test

    @Test
    void checkingTaskIfIdAreEqual() {
        taskManager.addTask(task1).setId(1);
        taskManager.addTask(task2).setId(1);
        assertEquals(task1, task2);
    }

    @Test
    void checkingEpicIfIdAreEqual() {
        taskManager.addEpic(epic1).setId(1);
        taskManager.addEpic(epic2).setId(1);
        assertEquals(epic1, epic2);
    }

    @Test
    void checkingSubtaskIfIdAreEqual() {
        Epic epic = new Epic(EPIC_NAME_TEXT, EPIC_DESCRIPTION_TEXT);
        SubTask subtask = new SubTask(SUBTASK_NAME_TEXT, SUBTASK_DESCRIPTION_TEXT, Status.NEW, epic.getId());
        SubTask subtask1 = new SubTask(SUBTASK_NAME_TEXT, SUBTASK_DESCRIPTION_TEXT, Status.NEW, epic.getId());

        taskManager.addSubtask(subtask).setId(1);
        taskManager.addSubtask(subtask1).setId(1);
        assertEquals(subtask, subtask1, "Экземпляры подкласса не равны друг другу");
    }

    @Test
    void canNotAddEpicToHimselfTest() {
        final int epicId = taskManager.addEpic(epic1).getId();
        epic1.addSubTaskId(epicId);
        assertTrue(epic1.subtaskIds.isEmpty(), "Эпик добавился сам в свои подзадачи");
    }

    @Test
    void setIdAndGenerateIdNotConflictTest() {
        final int taskId = taskManager.addTask(task1).getId();
        taskManager.updateTask(new Task(task1.getName(), task1.getDescription(), task1.getStatus(), taskId + 1));
        assertNull(taskManager.getTaskById(taskId + 1), "Задачи с заданным id и сгенерированным id конфликтуют внутри менеджера!");
    }

    @Test
    void immutabilityTaskTest() {
        final int taskId = taskManager.addTask(task1).getId();
        Task taskRef = new Task(task1.getName(), task1.getDescription(), task1.getStatus(), taskId);
        assertEquals(task1, taskRef, "Задача поменялась при добавлении в taskManager");
    }

    @Test
    void canNotAddSubTaskToHimselfEpicTest() {
        SubTask subtask = new SubTask(SUBTASK_NAME_TEXT, SUBTASK_DESCRIPTION_TEXT, Status.NEW, 1);
        assertThrows(IllegalArgumentException.class, () -> subtask.setId(1), "Подзадача сделала себя своим же эпиком");
    }

    @Test
    void addEpicWithTreeSubtaskTest(){
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subTask1);
        taskManager.addSubtask(subTask2);
        taskManager.addSubtask(subTask3);
        assertEquals(3, taskManager.getSubtasks().size(),"Неверное колличество подзадач!");
    }

    @Test
    void addEpicIsEmpty(){
        taskManager.addEpic(epic2);
        assertEquals(0, taskManager.getSubtasks().size(),"Эпик не пустой!");
    }
}