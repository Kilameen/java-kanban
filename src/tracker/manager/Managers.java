package tracker.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import tracker.server.adapter.DurationAdapter;
import tracker.server.adapter.LocalDateTimeAdapter;

import java.time.Duration;
import java.time.LocalDateTime;

public class Managers {
    private Managers(){

    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
