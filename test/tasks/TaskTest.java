package tasks;

import org.junit.jupiter.api.Test;
import taskTracker.tasks.Task;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static taskTracker.tasks.Status.NEW;

public class TaskTest {
    @Test
    public void addNewTask(){
    Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
    final int taskId = taskManager.addNewTask(task);

    final Task savedTask = taskManager.getTask(taskId);

    assertNotNull(savedTask, "Задача не найдена.");
    assertEquals(task, savedTask, "Задачи не совпадают.");

    final List<Task> tasks = taskManager.getTasks();

    assertNotNull(tasks, "Задачи не возвращаются.");
    assertEquals(1, tasks.size(), "Неверное количество задач.");
    assertEquals(task, tasks.get(0), "Задачи не совпадают.");
}
}
