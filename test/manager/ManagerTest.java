package manager;

import org.junit.jupiter.api.Test;
import tracker.manager.HistoryManager;
import tracker.manager.Managers;
import tracker.manager.TaskManager;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ManagerTest {
    @Test
    void getDefaultTest() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "Утилитарный класс вернул пустой объект taskManager");
    }

    @Test
    void getDefaultHistoryTest() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "Утилитарный класс вернул пустой объект historyManager");
    }
}