package tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskTracker.manager.Managers;
import taskTracker.manager.TaskManager;
import taskTracker.tasks.Epic;
import taskTracker.tasks.Status;
import taskTracker.tasks.SubTask;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SubtaskTest {

    static TaskManager taskManager;

    @BeforeEach
     void initSubtask() {
        taskManager = Managers.getDefault();
    }
    @Test
    void checkingSubtaskIfIdAreEqual(){
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        SubTask subtask = new SubTask("Test addNewSubtask", "Test addNewSubtask description", Status.NEW, epic.getId());
        SubTask subtask1 = new SubTask("Test addNewSubtask", "Test addNewSubtask description", Status.NEW, epic.getId());

        taskManager.addSubtask(subtask).setId(1);
        taskManager.addSubtask(subtask1).setId(1);
        assertEquals(subtask,subtask1, "Экземпляры подкласса не равны друг другу");
    }
    @Test
    void addNewSubtaskTest() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        final int epicId = taskManager.addEpic(epic).getId();

        SubTask subtask = new SubTask("Test addNewSubtask", "Test addNewSubtask description", Status.NEW,1);
        final int subtaskId = taskManager.addSubtask(subtask).getId();
        final SubTask savedSubtask = (SubTask) taskManager.getSubtaskById(subtaskId);

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(subtask, savedSubtask, "Задачи не совпадают.");

        final List<SubTask> subTasks = taskManager.getSubtasks();

        assertNotNull(subTasks, "Задачи не возвращаются.");
        assertEquals(1, subTasks.size(), "Неверное количество задач.");
        assertEquals(subtask, subTasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void canNotAddSubTaskToHimselfEpic() {
        SubTask subtask = new SubTask("Test addNewSubtask", "Test addNewSubtask description", Status.NEW,1);
        assertThrows(IllegalArgumentException.class, () -> subtask.setId(1),"Подзадача сделала себя своим же эпиком");

    }
}