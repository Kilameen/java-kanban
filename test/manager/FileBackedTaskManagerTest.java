package manager;

import org.junit.jupiter.api.*;
import tracker.exception.ManagerLoadException;
import tracker.manager.FileBackedTaskManager;
import tracker.tasks.Status;
import tracker.tasks.Task;
import java.io.File;
import java.io.IOException;
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
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubtasks();

        if (testFile.exists()) {
            testFile.delete();
        }
    }

    @Test
    void testAddTask() {
        Task task = new Task("Task_1", "Task_desc_1", Status.NEW);
        taskManager.addTask(task);
        assertEquals(1, taskManager.getTasks().size());
    }

    @Test
    void testSaveAndLoad() throws IOException {
        Task task = new Task("Task_1", "Task_desc_1", Status.NEW);

        taskManager.addTask(task);
        taskManager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);

        assertEquals(1, loadedManager.getTasks().size());
        assertEquals("Task_1", loadedManager.getTasks().get(0).getName());
    }

    @Test
    void testLoadEmptyFile() {
        assertThrows(ManagerLoadException.class, () -> {
            FileBackedTaskManager.loadFromFile(testFile);
        });
    }
}