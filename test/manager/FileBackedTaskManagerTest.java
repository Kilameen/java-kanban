package manager;

import org.junit.jupiter.api.Test;
import tracker.exception.ManagerLoadException;
import tracker.manager.FileBackedTaskManager;
import tracker.tasks.Epic;
import tracker.tasks.Status;
import tracker.tasks.SubTask;
import tracker.tasks.Task;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    static FileBackedTaskManager fileBackedTaskManager;
    static File testFile;

    @Override
    protected FileBackedTaskManager createTaskManager() throws IOException {
        testFile = new File("testData.csv");
        testFile.deleteOnExit();
        fileBackedTaskManager = new FileBackedTaskManager(testFile);
        return fileBackedTaskManager;
    }

    @Test
    void SaveAndLoadTaskTest() throws IOException {
        Task task = new Task("Task_1", Status.NEW, "Task_desc_1", LocalDateTime.now(), 15L);
        fileBackedTaskManager.addTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);
        Task loadedTask = loadedManager.getTaskById(1);
        assertEquals(task, loadedTask);
        assertEquals(1, loadedManager.getTasks().size());
    }

    @Test
    void testSaveAndLoadEpic() {
        Epic epic = new Epic("Epic_1", Status.NEW, "Epic_desc_1", LocalDateTime.now(), 15L);
        fileBackedTaskManager.addEpic(epic);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);
        Epic loadedEpic = loadedManager.getEpicById(1);
        assertEquals(epic, loadedEpic);
        assertEquals(1, loadedManager.getEpics().size());
    }

    @Test
    void testSaveAndLoadSubtask() {
        Epic epic = new Epic("Epic_1", Status.NEW, "Epic_desc_1", LocalDateTime.of(2020, 1, 1, 1, 1), 15L);
        fileBackedTaskManager.addEpic(epic);
        SubTask subtask = new SubTask("Subtask_1_1", Status.NEW, "Subtask_desc_1_1", epic.getEndTime().plusHours(1), 15L, epic.getId());
        fileBackedTaskManager.addSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);
        SubTask loadedSubTask = loadedManager.getSubtaskById(2);
        assertEquals(subtask, loadedSubTask);
        assertEquals(1, loadedManager.getSubtasks().size());
    }

    @Test
    void testLoadEmptyFile() {
        try {
            fileBackedTaskManager = FileBackedTaskManager.loadFromFile(testFile);
        } catch (ManagerLoadException e) {
            e.printStackTrace();
        }
    }
}