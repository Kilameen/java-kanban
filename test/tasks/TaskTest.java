package tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskTracker.manager.Managers;
import taskTracker.manager.TaskManager;
import taskTracker.tasks.Status;
import taskTracker.tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class TaskTest {
    static TaskManager taskManager;


    @BeforeEach
    void initTasks() {
        taskManager = Managers.getDefault();

    }
    @Test
    public void addNewTask(){
    Task task = new Task("Test addNewTask", "Test addNewTask description",Status.NEW);
        final int taskId = taskManager.addTask(task).getId();
        final Task savedTask = taskManager.getTaskById(taskId);

    assertNotNull(savedTask, "Задача не найдена.");
    assertEquals(task, savedTask, "Задачи не совпадают.");

    final List<Task> tasks = taskManager.getTasks();

    assertNotNull(tasks, "Задачи не возвращаются.");
    assertEquals(1, tasks.size(), "Неверное количество задач.");
    assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
}
}