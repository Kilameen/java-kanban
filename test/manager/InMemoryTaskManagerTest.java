package managerTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.exception.TaskOverlapException;
import tracker.manager.Managers;
import tracker.manager.TaskManager;
import tracker.tasks.Epic;
import tracker.tasks.Status;
import tracker.tasks.SubTask;
import tracker.tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {
    TaskManager taskManager;

    protected String TASK_NAME_TEXT = "Test addTask";
    protected String TASK_DESCRIPTION_TEXT = "Test addTask description";
    protected String EPIC_NAME_TEXT = "Test addEpic";
    protected String EPIC_DESCRIPTION_TEXT = "Test addEpic description";
    protected String SUBTASK_NAME_TEXT = "Test addSubtask";
    protected String SUBTASK_DESCRIPTION_TEXT = "Test addSubtask description";

    Task task1 = new Task(1,TASK_NAME_TEXT,Status.NEW, TASK_DESCRIPTION_TEXT, LocalDateTime.now(), Duration.ofMinutes(15));
    Task task2 = new Task(2,TASK_NAME_TEXT,Status.IN_PROGRESS, TASK_DESCRIPTION_TEXT, task1.getEndTime().plusHours(2), Duration.ofMinutes(15));

    Epic epic1 = new Epic(3,EPIC_NAME_TEXT, Status.NEW, EPIC_DESCRIPTION_TEXT,task2.getEndTime().plusHours(1),Duration.ofMinutes(15));
    Epic epic2 = new Epic(4,EPIC_NAME_TEXT,Status.DONE, EPIC_DESCRIPTION_TEXT,task2.getEndTime().plusHours(1),Duration.ofMinutes(15));

    SubTask subTask1 = new SubTask(5,SUBTASK_NAME_TEXT,Status.NEW, SUBTASK_DESCRIPTION_TEXT, task2.getEndTime().plusHours(1), Duration.ofMinutes(15), epic1.getId());
    SubTask subTask2 = new SubTask(6,SUBTASK_NAME_TEXT,Status.IN_PROGRESS, SUBTASK_DESCRIPTION_TEXT,subTask1.getEndTime().plusHours(1), Duration.ofMinutes(15), epic1.getId());
    SubTask subTask3 = new SubTask(7,SUBTASK_NAME_TEXT, Status.DONE, SUBTASK_DESCRIPTION_TEXT, subTask2.getEndTime().plusHours(1), Duration.ofMinutes(15), epic1.getId());

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
        taskManager.addTask(task1);
        final Task savedTask = taskManager.getTaskById(1);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task1, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task1, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void addNewEpicTest() {
        taskManager.addEpic(epic1);
        final Epic savedEpic = taskManager.getEpicById(1);

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic1, savedEpic, "Задачи не совпадают.");

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic1, epics.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void addNewSubtaskTest() {
        taskManager.addEpic(epic1);
        SubTask subtask = new SubTask(SUBTASK_NAME_TEXT, SUBTASK_DESCRIPTION_TEXT, Status.NEW, 1);
        taskManager.addSubtask(subtask);
        final SubTask savedSubtask = (SubTask) taskManager.getSubtaskById(2);

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
        taskManager.deleteEpic(epic1.getId());
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
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        task2.setId(1);
        task2.setStatus(Status.NEW);
        assertEquals(task1, task2);
    }

    @Test
    void checkingEpicIfIdAreEqual() {
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        epic2.setId(1);
        epic2.setStatus(Status.NEW);
        assertEquals(epic1, epic2);
    }

    @Test
    void checkingSubtaskIfIdAreEqual() {
        Epic epic1 = new Epic(EPIC_NAME_TEXT, EPIC_DESCRIPTION_TEXT);
        taskManager.addEpic(epic1);
        epic1.setId(1);
        SubTask subtask = new SubTask(SUBTASK_NAME_TEXT, SUBTASK_DESCRIPTION_TEXT, Status.NEW, epic1.getId());
        SubTask subtask1 = new SubTask(SUBTASK_NAME_TEXT, SUBTASK_DESCRIPTION_TEXT, Status.NEW, epic1.getId());

        taskManager.addSubtask(subtask);
        subtask.setId(2);
        taskManager.addSubtask(subtask1);
        subtask1.setId(2);
        assertEquals(subtask, subtask1, "Экземпляры подкласса не равны друг другу");
    }

    @Test
    void canNotAddEpicToHimselfTest() {
        taskManager.addEpic(epic1);
        epic1.addSubTaskId(1);
        assertTrue(epic1.subtaskIds.isEmpty(), "Эпик добавился сам в свои подзадачи");
    }

    @Test
    void setIdAndGenerateIdNotConflictTest() {
        taskManager.addTask(task1);
        taskManager.updateTask(new Task(task1.getName(), task1.getDescription(), task1.getStatus(), task1.getId() + 1));
        assertNull(taskManager.getTaskById(1 + 1), "Задачи с заданным id и сгенерированным id конфликтуют внутри менеджера!");
    }

    @Test
    void immutabilityTaskTest() {
        taskManager.addTask(task1);
        Task taskRef = new Task(task1.getName(), task1.getDescription(), task1.getStatus(), task1.getId());
        assertEquals(task1, taskRef, "Задача поменялась при добавлении в taskManager");
    }

    @Test
    void canNotAddSubTaskToHimselfEpicTest() {
        SubTask subtask = new SubTask(SUBTASK_NAME_TEXT, SUBTASK_DESCRIPTION_TEXT, Status.NEW, 1);
        assertThrows(IllegalArgumentException.class, () -> subtask.setId(1), "Подзадача сделала себя своим же эпиком");
    }

    @Test
    void addEpicWithTreeSubtaskTest() {
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subTask1);
        taskManager.addSubtask(subTask2);
        taskManager.addSubtask(subTask3);
        assertEquals(3, taskManager.getSubtasks().size(), "Неверное колличество подзадач!");
    }

    @Test
    void addEpicIsEmpty() {
        taskManager.addEpic(epic2);
        assertEquals(0, taskManager.getSubtasks().size(), "Эпик не пустой!");
    }

    @Test
    public void getPrioritizedTasksTest() {
        Task task1 = new Task(1,"Task_1",Status.NEW, "Task_desc_1",LocalDateTime.now(),Duration.ofMinutes(15));
        Task task2 = new Task(2,"Task_2", Status.IN_PROGRESS, "Task_desc_2", task1.getEndTime().plusHours(2), Duration.ofMinutes(15));
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        Set<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        assertEquals(2, prioritizedTasks.size(), "Должно быть 2 задачи");
        assertTrue(prioritizedTasks.contains(task1), "Список должен содержать task1");
        assertTrue(prioritizedTasks.contains(task2), "Список должен содержать task2");
    }
    @Test
    public void OverlappingTaskTest() {
        Task task1 = new Task(1,"Task_1",Status.NEW, "Task_desc_1",LocalDateTime.now(),Duration.ofMinutes(15));
        taskManager.addTask(task1);
        Task task2 = new Task(2,"Task_1",Status.NEW, "Task_desc_1",LocalDateTime.now(),Duration.ofMinutes(15));

        Exception exception = assertThrows(TaskOverlapException.class, () -> {
            taskManager.addTask(task2);
        },"Задачи пересекаются");
    }

@Test
public void epicStatusTest() {
    assertEquals(Status.NEW, taskManager.getEpicById(epic1.getId()).getStatus());
    subTask1.setStatus(Status.DONE);
    taskManager.updateSubtask(subTask1);
    taskManager.updateEpic(epic1);
    assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic1.getId()).getStatus());
    subTask2.setStatus(Status.DONE);
    taskManager.updateSubtask(subTask2);
    taskManager.updateEpic(epic1);
    assertEquals(Status.DONE, taskManager.getEpicById(epic1.getId()).getStatus());
}

@Test
public void epicStatusSubtaskStatusIN_PROGRESSTest() {
    subTask1.setStatus(Status.IN_PROGRESS);
    taskManager.updateSubtask(subTask1);
    taskManager.updateEpic(epic1);
    assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic1.getId()).getStatus());
}
}
