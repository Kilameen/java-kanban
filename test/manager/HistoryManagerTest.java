package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskTracker.manager.HistoryManager;
import taskTracker.manager.Managers;
import taskTracker.manager.TaskManager;
import taskTracker.tasks.Status;
import taskTracker.tasks.Task;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest {
    static HistoryManager historyManager;
    static TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault();
    }

    @Test
    void addNewHistoryTest() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
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
}
