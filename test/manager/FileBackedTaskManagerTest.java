package manager;

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
        Task task = new Task("Test addTask", "Test addTask description", Status.NEW);
        taskManager.addTask(task);
        assertEquals(1, taskManager.getTasks().size());
    }

    @Test
    void SaveAndLoadTaskTest() throws IOException {
        Task task = new Task("Test addTask", "Test addTask description", Status.NEW);
        taskManager.addTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);
        assertEquals(1, loadedManager.getTasks().size());
        assertEquals(task, loadedManager.getTasks().getFirst());
    }

    @Test
    void testSaveAndLoadEpic() {
        Epic epic = new Epic(1, "Test addEpic", "Test addEpic description", Status.NEW);
        taskManager.addEpic(epic);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);
        assertEquals(1, loadedManager.getEpics().size());
        assertEquals(epic, loadedManager.getEpics().getFirst());
    }

    @Test
    void testSaveAndLoadSubtask() {
        Epic epic = new Epic(1, "Test addEpic", "Test addEpic description", Status.NEW);
        taskManager.addEpic(epic);
        SubTask subtask = new SubTask(2, "Test addSubtask", "Test addSubtask description", Status.NEW, epic.getId());
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