package manager;

import org.junit.jupiter.api.Test;
import taskTracker.manager.HistoryManager;
import taskTracker.manager.Managers;
import taskTracker.manager.TaskManager;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ManagerTest {
    @Test
    void getDefault() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "Утилитарный класс вернул пустой объект taskManager");
    }

    @Test
    void getDefaultHistory() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "Утилитарный класс вернул пустой объект historyManager");
    }
}
