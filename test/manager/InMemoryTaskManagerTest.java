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

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
    }

    //Test add
    @Test
    void addTaskTest() {
        Task task = new Task(TASK_NAME_TEXT, TASK_DESCRIPTION_TEXT, Status.NEW);
        taskManager.addTask(task);
        assertEquals(1, taskManager.getTasks().size());
    }

    @Test
    void addEpicTest() {
        Epic epic = new Epic(EPIC_NAME_TEXT, EPIC_DESCRIPTION_TEXT);
        taskManager.addEpic(epic);
        assertEquals(1, taskManager.getEpics().size());
    }

    @Test
    void addSubtaskTest() {
        Epic epic = new Epic(EPIC_NAME_TEXT, EPIC_DESCRIPTION_TEXT);
        SubTask subTask = new SubTask(SUBTASK_NAME_TEXT, SUBTASK_DESCRIPTION_TEXT, Status.NEW, epic.getId());
        taskManager.addSubtask(subTask);
        assertEquals(1, taskManager.getSubtasks().size());
    }

    @Test
    public void addNewTaskTest() {
        Task task = new Task(TASK_NAME_TEXT, TASK_DESCRIPTION_TEXT, Status.NEW);
        final int taskId = taskManager.addTask(task).getId();
        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void addNewEpicTest() {
        Epic epic = new Epic(EPIC_NAME_TEXT, EPIC_DESCRIPTION_TEXT);
        final int epicId = taskManager.addEpic(epic).getId();
        final Epic savedEpic = taskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void addNewSubtaskTest() {
        Epic epic = new Epic(EPIC_NAME_TEXT, EPIC_DESCRIPTION_TEXT);
        final int epicId = taskManager.addEpic(epic).getId();

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
        Task task = new Task(TASK_NAME_TEXT, TASK_DESCRIPTION_TEXT, Status.NEW);
        taskManager.addTask(task);
        assertEquals(1, taskManager.getTaskById(1).getId());
    }

    @Test
    void getEpicTest() {
        Epic epic = new Epic(EPIC_NAME_TEXT, EPIC_DESCRIPTION_TEXT);
        taskManager.addEpic(epic);
        assertEquals(1, taskManager.getEpicById(1).getId());
    }

    @Test
    void getSubtaskTest() {
        Epic epic = new Epic(EPIC_NAME_TEXT, EPIC_DESCRIPTION_TEXT);
        SubTask subTask = new SubTask(SUBTASK_NAME_TEXT, SUBTASK_DESCRIPTION_TEXT, Status.NEW, epic.getId());
        taskManager.addSubtask(subTask);
        assertEquals(1, taskManager.getSubtaskById(1).getId());
    }

    //Test delete

    @Test
    void deleteTaskByIdTest() {
        Task task = new Task(TASK_NAME_TEXT, TASK_DESCRIPTION_TEXT, Status.NEW);
        taskManager.addTask(task);
        taskManager.deleteTask(1);
        assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    void deleteAllTaskTest() {
        Task task = new Task(TASK_NAME_TEXT, TASK_DESCRIPTION_TEXT, Status.NEW);
        taskManager.addTask(task);
        taskManager.deleteAllTasks();
        assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    void deleteEpicByIdTest() {
        Epic epic = new Epic(EPIC_NAME_TEXT, EPIC_DESCRIPTION_TEXT);
        taskManager.addEpic(epic);
        taskManager.deleteEpic(1);
        assertEquals(0, taskManager.getEpics().size());
    }

    @Test
    void deleteEpicAllTest() {
        Epic epic = new Epic(EPIC_NAME_TEXT, EPIC_DESCRIPTION_TEXT);
        taskManager.addEpic(epic);
        taskManager.deleteAllEpics();
        assertEquals(0, taskManager.getEpics().size());
    }

    @Test
    void deleteSubTaskByIdTest() {
        Epic epic = new Epic(EPIC_NAME_TEXT, EPIC_DESCRIPTION_TEXT);
        SubTask subTask = new SubTask(SUBTASK_NAME_TEXT, SUBTASK_DESCRIPTION_TEXT, Status.NEW, epic.getId());

        taskManager.addEpic(epic);
        taskManager.addSubtask(subTask);
        taskManager.deleteSubtask(2);

        assertEquals(0, taskManager.getSubtasks().size());
        assertEquals(0, taskManager.getEpicById(1).getSubtasksByEpic().size());
    }

    @Test
    void deleteAllSubTasksTest() {
        Epic epic = new Epic(EPIC_NAME_TEXT, EPIC_DESCRIPTION_TEXT);
        SubTask subTask1 = new SubTask(SUBTASK_NAME_TEXT, SUBTASK_DESCRIPTION_TEXT, Status.NEW, epic.getId());
        SubTask subTask2 = new SubTask(SUBTASK_NAME_TEXT, SUBTASK_DESCRIPTION_TEXT, Status.NEW, epic.getId());

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
        Task task = new Task(TASK_NAME_TEXT, TASK_DESCRIPTION_TEXT, Status.NEW);
        taskManager.addTask(task);
        Task updatedTask = taskManager.getTaskById(1);
        updatedTask.setDescription("Test: changed description");
        taskManager.updateTask(updatedTask);
        assertEquals("Test: changed description", taskManager.getTaskById(1).getDescription());
    }

    @Test
    void updateEpicTest() {
        Epic epic = new Epic(EPIC_NAME_TEXT, EPIC_DESCRIPTION_TEXT);
        taskManager.addEpic(epic);
        Epic updatedEpic = taskManager.getEpicById(1);
        updatedEpic.setDescription("Test: changed description");
        taskManager.updateEpic(updatedEpic);
        assertEquals("Test: changed description", taskManager.getEpicById(1).getDescription());
    }

    @Test
    void updateSubTaskByIdTest() {
        Epic epic = new Epic(EPIC_NAME_TEXT, EPIC_DESCRIPTION_TEXT);
        SubTask subTask = new SubTask(SUBTASK_NAME_TEXT, SUBTASK_DESCRIPTION_TEXT, Status.NEW, 1);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subTask);
        SubTask updatedSubTask = (SubTask) taskManager.getSubtaskById(2);
        updatedSubTask.setStatus(Status.DONE);
        taskManager.updateSubtask(updatedSubTask);
        assertEquals(Status.DONE, taskManager.getSubtaskById(2).getStatus());
    }

//Check Test

    @Test
    void checkingTaskIfIdAreEqual() {
        Task task = new Task(TASK_NAME_TEXT, TASK_DESCRIPTION_TEXT, Status.NEW);
        Task task1 = new Task(TASK_NAME_TEXT, TASK_DESCRIPTION_TEXT, Status.NEW);

        taskManager.addTask(task).setId(1);
        taskManager.addTask(task1).setId(1);
        assertEquals(task, task1);
    }

    @Test
    void checkingEpicIfIdAreEqual() {
        Epic epic = new Epic(EPIC_NAME_TEXT, EPIC_DESCRIPTION_TEXT);
        Epic epic1 = new Epic(EPIC_NAME_TEXT, EPIC_DESCRIPTION_TEXT);

        taskManager.addEpic(epic).setId(1);
        taskManager.addEpic(epic1).setId(1);
        assertEquals(epic, epic1);
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
        Epic epic = new Epic(EPIC_NAME_TEXT, EPIC_DESCRIPTION_TEXT);
        final int epicId = taskManager.addEpic(epic).getId();
        epic.addSubTaskId(epicId);
        assertTrue(epic.subtaskIds.isEmpty(), "Эпик добавился сам в свои подзадачи");
    }

    @Test
    void setIdAndGenerateIdNotConflictTest() {
        Task task = new Task(TASK_NAME_TEXT, TASK_DESCRIPTION_TEXT, Status.NEW);
        final int taskId = taskManager.addTask(task).getId();
        taskManager.updateTask(new Task(task.getName(), task.getDescription(), task.getStatus(), taskId + 1));
        assertNull(taskManager.getTaskById(taskId + 1), "Задачи с заданным id и сгенерированным id конфликтуют внутри менеджера!");
    }

    @Test
    void immutabilityTaskTest() {
        Task task = new Task(TASK_NAME_TEXT, TASK_DESCRIPTION_TEXT, Status.NEW);
        final int taskId = taskManager.addTask(task).getId();
        Task taskRef = new Task(task.getName(), task.getDescription(), task.getStatus(), taskId);
        assertEquals(task, taskRef, "Задача поменялась при добавлении в taskManager");
    }

    @Test
    void canNotAddSubTaskToHimselfEpic() {
        SubTask subtask = new SubTask(SUBTASK_NAME_TEXT, SUBTASK_DESCRIPTION_TEXT, Status.NEW, 1);
        assertThrows(IllegalArgumentException.class, () -> subtask.setId(1), "Подзадача сделала себя своим же эпиком");
    }
}