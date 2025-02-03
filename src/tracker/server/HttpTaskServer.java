package tracker.server;

import com.sun.net.httpserver.HttpServer;
import tracker.manager.Managers;
import tracker.manager.TaskManager;
import tracker.server.handler.*;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);

        httpServer.createContext("/tasks", new TaskHandler(taskManager));
        httpServer.createContext("/epics", new EpicHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedTasksHandler(taskManager));
    }

    public static void main(String[] args) {
        try {
            TaskManager manager = Managers.getDefault();
            HttpTaskServer server = new HttpTaskServer(manager);
            server.start();
        } catch (IOException e) {
            System.err.println("Не удалось запустить сервер");
        }
    }

    public void start() throws IOException {
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту");
    }

    public void stop() {
        try {
            httpServer.stop(0);
            System.out.println("HTTP-сервер остановлен");
        } catch (Exception e) {
            System.err.println("Не удалось остановить сервер");
        }
    }
}