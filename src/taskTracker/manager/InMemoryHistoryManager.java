package taskTracker.manager;

import taskTracker.tasks.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int MAX_HISTORY_STORAGE = 10;
    private final List<Task> historyList;
    public InMemoryHistoryManager() {
        this.historyList = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        if (historyList.size() >= MAX_HISTORY_STORAGE) {
            historyList.removeFirst();
        }
        historyList.add(task);

    }
    @Override
    public List<Task> getHistory() {
        return historyList;
    }
}
