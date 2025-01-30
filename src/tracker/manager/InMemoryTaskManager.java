package tracker.manager;

import tracker.exception.TaskOverlapException;
import tracker.tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, SubTask> subtasks;

    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private int nextId;
    protected Set<Task> prioritizedTasks;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        nextId = 1;
        prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }

    //allGet

    @Override
    public List<Task> getTasks() {
        return tasks.values().stream().toList();
    }

    @Override
    public List<Epic> getEpics() {
        return epics.values().stream().toList();
    }

    @Override
    public List<SubTask> getSubtasks() {
        return subtasks.values().stream().toList();
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public SubTask getSubtaskById(int id) {
        historyManager.add(subtasks.get(id));
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
        return epic.getSubtasksByEpic().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

//allAdd

    @Override
    public void addTask(Task task) {

        if (isOverlapping(task)) {
            throw new TaskOverlapException("Время задач пересекается");
        }
        task.setId(nextId++);
        tasks.put(task.getId(), task);

        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void addEpic(Epic epic) {
        if (isOverlapping(epic)) {
            throw new TaskOverlapException("Время задач пересекается");
        }
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);

    }

    @Override
    public void addSubtask(SubTask subtask) {
        if (isOverlapping(subtask)) {
            throw new TaskOverlapException("Время задач пересекается");
        }
        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicID());

        if (epic == null) {
            return;
        }
        epic.getSubtasksByEpic().add(subtask.getId());
        updateEpicStatus(epic.getId());

        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }
    }

    //allUpdate

    @Override
    public void updateTask(Task task) {
        int taskID = task.getId();
        if (tasks.containsKey(taskID)) {
            tasks.put(taskID, task);

            prioritizedTasks.removeIf(t -> t.getId() == taskID);
        }
        if (isOverlapping(task)) {
            throw new TaskOverlapException("Время задач пересекается");
        }
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        int epicID = epic.getId();
        if (epics.containsKey(epicID)) {
            epics.put(epicID, epic);
            updateEpicStatus(epicID);
        }
        if (isOverlapping(epic)) {
            throw new TaskOverlapException("Время задач пересекается");
        }
    }

    @Override
    public void updateSubtask(SubTask subtask) {
        int subTaskId = subtask.getId();
        int epicID = subtask.getEpicID();
        if (subtasks.containsKey(subTaskId)) {
            subtasks.put(subTaskId, subtask);
            updateEpicStatus(epicID);
        }
        prioritizedTasks.removeIf(s -> s.getId() == subTaskId);
        if (isOverlapping(subtask)) {
            throw new TaskOverlapException("Время задач пересекается");
        }
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }
    }

    private void updateEpicStatus(int id) {
        List<Integer> listSubtaskByEpic = epics.get(id).getSubtasksByEpic();
        int resultStatus = listSubtaskByEpic.stream()
                .map(subtasks::get)
                .mapToInt(subtask -> {
                    if (subtask.getStatus() == Status.NEW) return -1;
                    if (subtask.getStatus() == Status.DONE) return 1;
                    return 0;
                })
                .sum();

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
        for (Integer task : tasks.keySet()) {
            historyManager.remove(task);
            prioritizedTasks.remove(tasks.get(task));
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
            for (Integer i : epics.keySet()) {
                historyManager.remove(i);
            }
            for (Integer id : subtasks.keySet()) {
                historyManager.remove(id);
            }
            epics.clear();
            subtasks.clear();
        }

    @Override
    public void deleteAllSubtasks() {
        for (Integer i : subtasks.keySet()) {
            historyManager.remove(i);
            prioritizedTasks.remove(subtasks.get(i));
        }
        for (Epic epic : epics.values()) {
            epic.setStatus(Status.NEW);
            epic.setDuration(Duration.ofMinutes(0));
            epic.setStartTime(LocalDateTime.now());
        }
        subtasks.clear();
    }

    @Override
    public void deleteTask(int id) {
        Task deleteTaskId = tasks.remove(id);
        if (deleteTaskId != null) {
            prioritizedTasks.remove(deleteTaskId);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteEpic(int id) {
        Epic removedEpic = epics.remove(id);
        if (removedEpic != null) {
            removedEpic.getSubtasksByEpic().forEach(subtasks::remove);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteSubtask(int subTaskId) {
        SubTask subtask = subtasks.remove(subTaskId);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicID());
            epic.getSubtasksByEpic().remove((Integer) subTaskId);
            updateEpicStatus(epic.getId());
        }
        prioritizedTasks.remove(subtask);
        historyManager.remove(subTaskId);
    }

    private boolean isOverlapping(Task validTask) {
        if (validTask.getStartTime() == null || validTask.getDuration() == null) {
            return false;
        }
        return prioritizedTasks.stream()
                .anyMatch(task -> task.getEndTime().isAfter(validTask.getStartTime()) &&
                        task.getStartTime().isBefore(validTask.getEndTime())
                );
    }
}