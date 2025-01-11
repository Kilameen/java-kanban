package managerTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.exception.ManagerLoadException;
import tracker.manager.FileBackedTaskManager;
import tracker.tasks.Epic;
import tracker.tasks.Status;
import tracker.tasks.SubTask;
import tracker.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileBackedTaskManagerTest {

    private FileBackedTaskManager taskManager;
    private File testFile;

    @BeforeEach
    void setUp() {
        testFile = new File("testData.csv");
        taskManager = new FileBackedTaskManager(testFile);
    }

    @AfterEach
    void tearDown() {
        taskManager.deleteAll();

        if (testFile.exists()) {
            testFile.delete();
        }
    }

    @Test
    void testAddTask() {
        Task task = new Task(1,"Task_1",Status.NEW, "Task_desc_1",LocalDateTime.now(),Duration.ofMinutes(15));
        taskManager.addTask(task);
        assertEquals(1, taskManager.getTasks().size());
    }

    @Test
    void SaveAndLoadTaskTest() throws IOException {
        Task task = new Task(1,"Task_1",Status.NEW, "Task_desc_1",LocalDateTime.now(),Duration.ofMinutes(15));
        taskManager.addTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);
        assertEquals(1, loadedManager.getTasks().size());
        assertEquals(task, loadedManager.getTasks().getFirst());
    }

    @Test
    void testSaveAndLoadEpic() {
        Epic epic = new Epic(1,"Epic_1", Status.NEW ,"Epic_desc_1", LocalDateTime.now(), Duration.ofMinutes(15));
        taskManager.addEpic(epic);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);
        assertEquals(1, loadedManager.getEpics().size());
        assertEquals(epic, loadedManager.getEpics().getFirst());
    }

    @Test
    void testSaveAndLoadSubtask() {
        Epic epic = new Epic(1,"Epic_1", Status.NEW ,"Epic_desc_1", LocalDateTime.of(2020, 1, 1, 1, 1), Duration.ofMinutes(15));
        taskManager.addEpic(epic);
        SubTask subtask = new SubTask(2,"Subtask_1_1", Status.IN_PROGRESS,"Subtask_desc_1_1", epic.getEndTime().plusHours(1), Duration.ofMinutes(15), epic.getId());
        taskManager.addSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);
        assertEquals(1, loadedManager.getSubtasks().size());
        assertEquals(subtask, loadedManager.getSubtasks().getFirst());
    }

    @Test
    void testLoadEmptyFile() {
        assertThrows(ManagerLoadException.class, () -> {
            FileBackedTaskManager.loadFromFile(testFile);
        });
    }
}