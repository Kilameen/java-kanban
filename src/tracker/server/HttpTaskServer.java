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

    public HttpTaskServer() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);

        TaskManager TASK_MANAGER = Managers.getDefault();
        httpServer.createContext("/tasks", new TaskHandler(TASK_MANAGER));
        httpServer.createContext("/epics", new EpicHandler(TASK_MANAGER));
        httpServer.createContext("/subtasks", new SubtaskHandler(TASK_MANAGER));
        httpServer.createContext("/history", new HistoryHandler(TASK_MANAGER));
        httpServer.createContext("/prioritized", new PrioritizedTasksHandler(TASK_MANAGER));
    }

    public static void main(String[] args) {
        try {
            HttpTaskServer server = new HttpTaskServer();
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