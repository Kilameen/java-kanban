package taskTracker.manager;

import taskTracker.tasks.Epic;
import taskTracker.tasks.Status;
import taskTracker.tasks.SubTask;
import taskTracker.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, SubTask> subtasks;
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private int nextId;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        nextId = 1;
    }

    //allGet

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<SubTask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    @Override
    public SubTask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    @Override
    public List<Task> getHistory() {
return historyManager.getHistory();
    }

    @Override
    public List<SubTask> getSubtasksByEpicId(int id) {
        Epic epic = epics.get(id);
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

    @Override
    public Task addTask(Task task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic addEpic(Epic epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);

        return epic;
    }

    @Override
    public SubTask addSubtask(SubTask subtask) {
        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicID());

        if (epic == null) {
            return subtask;
        }
        epic.getSubtasksByEpic().add(subtask.getId());
        updateStatusEpicId(epic.getId());
        return subtask;
    }
    //allUpdate

    @Override
    public void updateTask(Task task) {
        int taskID=task.getId();
        if (tasks.containsKey(taskID)) {
            tasks.put(taskID, task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        int epicID=epic.getId();
        if (epics.containsKey(epicID)) {
            epics.put(epicID, epic);
        }
    }

    @Override
    public void updateSubtask(SubTask subtask) {
        int subTaskId=subtask.getId();
        if (subtasks.containsKey(subTaskId)) {
            subtasks.put(subTaskId, subtask);

            int epicID =subtask.getEpicID();
                updateStatusEpicId(epicID);
            }
        }
@Override
public void updateStatusEpicId(int id) {
    int resultStatus = 0;
    List<Integer> listSubtaskByEpic = epics.get(id).getSubtasksByEpic();
    for (Integer integer : listSubtaskByEpic) {

        if (subtasks.get(integer).getStatus() == Status.NEW) {
            resultStatus--;
        }
        if (subtasks.get(integer).getStatus() == Status.DONE) {
            resultStatus++;
        }
    }
    if (resultStatus == listSubtaskByEpic.size() && !listSubtaskByEpic.isEmpty()) {
        epics.get(id).setStatus(Status.DONE);
    } else if (resultStatus == (-listSubtaskByEpic.size()) || listSubtaskByEpic.isEmpty()) {
        epics.get(id).setStatus(Status.NEW);
    } else {
        epics.get(id).setStatus(Status.IN_PROGRESS);
    }
}

    //allDelete

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
       epics.clear();
       subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtasksByEpic().clear();
            updateStatusEpicId(epic.getId());

        }
        subtasks.clear();
    }
    @Override
    public void deleteTask(int id) {
        Task removedTask = tasks.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        Epic removedEpic = epics.remove(id);
        if (removedEpic != null) {
            for (Integer subtaskId : removedEpic.getSubtasksByEpic()) {
                subtasks.remove(subtaskId);
            }
        }
    }

    @Override
    public void deleteSubtask(int id) {
        SubTask removedSubtask = subtasks.remove(id);
        if (removedSubtask != null) {
            Epic epic = epics.get(removedSubtask.getEpicID());
            if (epic != null) {
                epic.getSubtasksByEpic().remove((Integer) id);
                updateStatusEpicId(epic.getId());
            }
        }
    }


}