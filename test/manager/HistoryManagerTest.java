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

    @BeforeEach
    void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault();
    }

    @Test
    void addNewHistoryTest() {
        Task task = new Task(TASK_NAME_TEXT, TASK_DESCRIPTION_TEXT, Status.NEW);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void historyVersionTest() {
        Task task = new Task(TASK_NAME_TEXT, TASK_DESCRIPTION_TEXT, Status.NEW);
        final int taskId = taskManager.addTask(task).getId();
        taskManager.getTaskById(taskId);
        assertEquals(1, taskManager.getHistory().size(), "История просмотров не сохранена!");
        taskManager.updateTask(new Task(task.getName(), task.getDescription(), Status.IN_PROGRESS, task.getId()));
        taskManager.getTaskById(taskId);
        assertEquals(2, taskManager.getHistory().size(), "История просмотров не сохранена!");
        assertNotEquals(taskManager.getHistory().getFirst(), taskManager.getHistory().getLast(), "История не сохраняет предыдущую версию задачи!");
    }
}
