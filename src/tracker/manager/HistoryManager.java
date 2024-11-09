package tracker.manager;

import tracker.tasks.Task;
import java.util.List;

public interface HistoryManager {
    void add(Task task);

    List<Task> getHistory();
}
