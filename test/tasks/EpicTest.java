package tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskTracker.manager.Managers;
import taskTracker.manager.TaskManager;
import taskTracker.tasks.Epic;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {
    static TaskManager taskManager;

    @BeforeEach
    void initEpic() {
        taskManager = Managers.getDefault();
    }

    @Test
    void checkingEpicIfIdAreEqual(){
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        Epic epic1 = new Epic("Test addNewEpic", "Test addNewEpic description");

        taskManager.addEpic(epic).setId(1);
        taskManager.addEpic(epic1).setId(1);
        assertEquals(epic,epic1);
    }


    @Test
    void addNewEpicTest() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        final int epicId= taskManager.addEpic(epic).getId();
        final Epic savedEpic = taskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.getFirst(), "Задачи не совпадают.");
    }
    @Test
    void canNotAddEpicToHimselfTest(){
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        final int epicId= taskManager.addEpic(epic).getId();
        epic.addSubTaskId(epicId);
        assertTrue(epic.subtaskIds.isEmpty(), "Эпик добавился сам в свои подзадачи");
    }


    }
