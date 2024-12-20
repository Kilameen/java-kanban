package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.manager.HistoryManager;
import tracker.manager.Managers;
import tracker.manager.TaskManager;
import tracker.tasks.Status;
import tracker.tasks.Task;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest {
    static HistoryManager historyManager;
    static TaskManager taskManager;

    protected String TASK_NAME_TEXT = "Test addTask";
    protected String TASK_DESCRIPTION_TEXT = "Test addTask description";

    Task task1 = new Task(TASK_NAME_TEXT, TASK_DESCRIPTION_TEXT, Status.NEW);
    Task task2 = new Task(TASK_NAME_TEXT, TASK_DESCRIPTION_TEXT, Status.NEW);
    Task task3 = new Task(TASK_NAME_TEXT, TASK_DESCRIPTION_TEXT, Status.NEW);

    public void addTaskToHistory() {
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        final int taskId1 = task1.getId();
        final int taskId2 = task2.getId();
        final int taskId3 = task3.getId();

        task1.setId(taskId1);
        task2.setId(taskId2);
        task3.setId(taskId3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
    }

    @BeforeEach
    void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault();
    }

    @Test
    void addNewHistoryTest() {
        taskManager.addTask(task1);
        historyManager.add(task1);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void historyVersionTest() {
        taskManager.addTask(task1);
        final int taskId = task1.getId();
        taskManager.getTaskById(taskId);
        assertEquals(1, taskManager.getHistory().size(), "История просмотров не сохранена!");
    }

    @Test
    public void shouldAddTasksToHistory() {
        addTaskToHistory();
        assertEquals(List.of(task1, task2, task3), historyManager.getHistory(),"Задача не добавилась");
    }

    @Test
    public void shouldRemoveTask() {
        addTaskToHistory();
        historyManager.remove(task2.getId());
        assertEquals(List.of(task1, task3), historyManager.getHistory(),"Задача не была удалена!");
    }

    @Test
    public void checkingTheOrderOfTasksInTheHistoryForRepeatedRequests(){
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(1);

        assertEquals(List.of(task2, task1), taskManager.getHistory(),"Задача была добавлена!");
    }
}