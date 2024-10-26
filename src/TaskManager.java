import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, EpicTask> epics;
    private final HashMap<Integer, SubTask> subtasks;
    private int nextId;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        nextId = 1;
    }

    //allGet

    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<EpicTask> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public List<SubTask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Task getEpic(int id) {
        return epics.get(id);
    }

    public Task getSubtask(int id) {
        return subtasks.get(id);
    }

    public List<SubTask> getSubtasks(int epicID) {
        EpicTask epic = epics.get(epicID);
        if (epic == null) return new ArrayList<>();

        List<SubTask> subtasksList = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtasksByEpic()) {
            SubTask subtask = subtasks.get(subtaskId);
            if (subtask != null) {
                subtasksList.add(subtask);
            }
        }
        return subtasksList;
    }

//allAdd

    public void addTask(Task task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
    }

    public void addEpic(EpicTask epicTask) {
        epicTask.setId(nextId++);
        epics.put(epicTask.getId(), epicTask);
    }

    public void addSubtask(SubTask subtask) {
        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);
        EpicTask epicTask = epics.get(subtask.getEpicID());

        if (epicTask == null) {
            return;
        }
        epicTask.getSubtasksByEpic().add(subtask.getId());
        epicTask.updateStatus(this);
    }

//allUpdate

    public void updateTask(int id, Task task) {
        if (tasks.containsKey(id)) {
            tasks.put(id, task);
        }
    }

    public void updateEpic(int id, EpicTask epicTask) {
        if (epics.containsKey(id)) {
            epics.put(id, epicTask);
        }
    }

    public void updateSubtask(int id, SubTask subtask) {
        if (subtasks.containsKey(id)) {
            subtasks.put(id, subtask);
            EpicTask epic = epics.get(subtask.getEpicID());
            if (epic != null) {
                epic.updateStatus(this);
            }
        }
    }

    //allDelete

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        for (EpicTask epicTask : epics.values()) {
            for (Integer subtaskId : epicTask.getSubtasksByEpic()) {
                subtasks.remove(subtaskId);
            }
        }
        epics.clear();
    }

    public void deleteAllSubtasks() {
        for (EpicTask epicTask : epics.values()) {
            epicTask.getSubtasksByEpic().clear();
            epicTask.updateStatus(this);

        }
        subtasks.clear();
    }
    public void deleteTask(int id) {
        Task removedTask = tasks.remove(id);
    }

    public void deleteEpic(int id) {
        EpicTask removedEpic = epics.remove(id);
        if (removedEpic != null) {
            for (Integer subtaskId : removedEpic.getSubtasksByEpic()) {
                subtasks.remove(subtaskId);
            }
        }
    }

    public void deleteSubtask(int id) {
        SubTask removedSubtask = subtasks.remove(id);
        if (removedSubtask != null) {
            EpicTask epic = epics.get(removedSubtask.getEpicID());
            if (epic != null) {
                epic.getSubtasksByEpic().remove((Integer) id);
                epic.updateStatus(this);
            }
        }
    }
}