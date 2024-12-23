package tracker.manager;

import tracker.tasks.Epic;
import tracker.tasks.SubTask;
import tracker.tasks.Task;
import java.util.List;

public interface TaskManager {

    List<Task> getTasks();

    List<Epic> getEpics();

    List<SubTask> getSubtasks();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Task getSubtaskById(int id);

    List<Task> getHistory();

    List<SubTask> getSubtasksByEpicId(int id);

    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubtask(SubTask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(SubTask subtask);

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    void deleteTask(int id);

    void deleteEpic(int id);

    void deleteSubtask(int id);

    void deleteAll();
}