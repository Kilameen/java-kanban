package taskTracker.manager;

import taskTracker.tasks.Epic;
import taskTracker.tasks.Status;
import taskTracker.tasks.SubTask;
import taskTracker.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
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

    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public List<SubTask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Task getEpicById(int id) {
        return epics.get(id);
    }

    public Task getSubtaskById(int id) {
        return subtasks.get(id);
    }

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

    public void addTask(Task task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
    }

    public void addEpic(Epic epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
    }

    public void addSubtask(SubTask subtask) {
        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicID());

        if (epic == null) {
            return;
        }
        epic.getSubtasksByEpic().add(subtask.getId());
        updateStatusEpicId(epic.getId());
    }

//allUpdate

    public void updateTask(Task task) {
        int taskID=task.getId();
        if (tasks.containsKey(taskID)) {
            tasks.put(taskID, task);
        }
    }

    public void updateEpic(Epic epic) {
        int epicID=epic.getId();
        if (epics.containsKey(epicID)) {
            epics.put(epicID, epic);
        }
    }

    public void updateSubtask(SubTask subtask) {
        int subTaskId=subtask.getId();
        if (subtasks.containsKey(subTaskId)) {
            subtasks.put(subTaskId, subtask);

            int epicID =subtask.getEpicID();
                updateStatusEpicId(epicID);
            }
        }
private void updateStatusEpicId(int id) {
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

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
       epics.clear();
       subtasks.clear();
    }

    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtasksByEpic().clear();
            updateStatusEpicId(epic.getId());

        }
        subtasks.clear();
    }
    public void deleteTask(int id) {
        Task removedTask = tasks.remove(id);
    }

    public void deleteEpic(int id) {
        Epic removedEpic = epics.remove(id);
        if (removedEpic != null) {
            for (Integer subtaskId : removedEpic.getSubtasksByEpic()) {
                subtasks.remove(subtaskId);
            }
        }
    }

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