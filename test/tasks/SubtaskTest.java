package tasks;

import org.junit.jupiter.api.BeforeAll;
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

    @BeforeAll
    static void initSubtask() {
        taskManager = Managers.getDefault();
    }

    @Test
    void addNewSubtask() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        final int epicId = taskManager.addEpic(epic).getId();

        SubTask subtask = new SubTask("Test addNewSubtask", "Test addNewSubtask description", Status.NEW, epic.getId());
        final int subtaskId = taskManager.addSubtask(subtask).getId();
        final SubTask savedSubtask = (SubTask) taskManager.getSubtaskById(subtaskId);

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(subtask, savedSubtask, "Задачи не совпадают.");

        final List<SubTask> subTasks = taskManager.getSubtasks();

        assertNotNull(subTasks, "Задачи не возвращаются.");
        assertEquals(1, subTasks.size(), "Неверное количество задач.");
        assertEquals(subtask, subTasks.getFirst(), "Задачи не совпадают.");
    }

//    @Test
//    void canNotAddSubTaskToHimselfEpic() {
//        Epic epic = new Epic("Test addNewEpicTask", "Test addNewEpicTask description");
//        final int epicId = taskManager.addEpic(epic).getId();
//
//        SubTask subTask = new SubTask("Test addNewSubTask", "Test addNewSubTask description",Status.NEW, epicId);
//        final int subTaskId = taskManager.addSubtask(subTask).getId();
//
//
//    }
}