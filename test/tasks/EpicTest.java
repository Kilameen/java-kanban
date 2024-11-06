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
    void addNewEpic() {
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
    void canNotAddEpicToHimself(){
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        final int epicId= taskManager.addEpic(epic).getId();
        epic.addSubTaskId(epicId);
        assertTrue(epic.subtaskIds.isEmpty(), "Эпик добавился сам в свои подзадачи");
    }


    }
