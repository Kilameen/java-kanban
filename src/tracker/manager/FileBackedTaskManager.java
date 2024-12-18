package tracker.manager;

import tracker.exception.ManagerLoadException;
import tracker.exception.ManagerSaveException;
import tracker.tasks.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;
    private static final String FILE_TITLE = String.join(",", "id", "type", "name", "status", "description", "epic");

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() {
        try (Writer writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write(FILE_TITLE);
            writer.write("\n");

            for (Task task : getTasks()) {
                writer.write(taskToString(task));
                writer.write("\n");
            }

            for (Task task : getEpics()) {
                writer.write(taskToString(task));
                writer.write("\n");
            }

            for (Task task : getSubtasks()) {
                writer.write(taskToString(task));
                writer.write("\n");
            }

        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    public void load() {
        try (Reader reader = new FileReader(file, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            bufferedReader.readLine();

            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                if (line.isEmpty()) {
                    continue;
                }

                Task task = fromString(line);
                if (task instanceof Epic) {
                    addEpic((Epic) task);
                } else if (task instanceof SubTask) {
                    addSubtask((SubTask) task);
                } else {
                    addTask(task);
                }
            }
        } catch (IOException e) {
            throw new ManagerLoadException(e.getMessage());
        }
    }

    private String taskToString(Task task) {
        StringBuilder builder = new StringBuilder();
        builder.append(task.getId()).append(",");
        builder.append(task.getType()).append(",");
        builder.append(task.getName()).append(",");
        builder.append(task.getStatus()).append(",");
        builder.append(task.getDescription()).append(",");
        if (task instanceof SubTask) {
            builder.append(((SubTask) task).getEpicID()).append(",");
        } else {
            builder.append(",");
        }
        return builder.toString();
    }

    private Task fromString(String line) {
        String[] parameters = line.split(",");
        Type type = Type.valueOf(parameters[1]);
        Task task;
        try {
            switch (type) {
                case TASK:
                    task = new Task(Integer.parseInt(parameters[0]), parameters[2], parameters[4], Status.valueOf(parameters[3]));
                    break;
                case EPIC:
                    task = new Epic(Integer.parseInt(parameters[0]), parameters[2], parameters[4], Status.valueOf(parameters[3]));
                    break;
                case SUBTASK:
                    task = new SubTask(Integer.parseInt(parameters[0]), parameters[2], parameters[4], Status.valueOf(parameters[3]), Integer.parseInt(parameters[5]));
                    break;
                default:
                    throw new ManagerSaveException("Неизвестный тип задачи: " + type);
            }
        } catch (Exception e) {
            throw new ManagerSaveException(e.getMessage());
        }
        return task;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        manager.load();
        return manager;
    }

    @Override
    public Task addTask(Task task) {
        return super.addTask(task);
    }

    @Override
    public Epic addEpic(Epic epic) {
        return super.addEpic(epic);
    }

    @Override
    public SubTask addSubtask(SubTask subtask) {
        return super.addSubtask(subtask);
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
    }

    @Override
    public void updateSubtask(SubTask subtask) {
        super.updateSubtask(subtask);
    }

    @Override
    public void updateStatusEpicId(int id) {
        super.updateStatusEpicId(id);
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
    }
}