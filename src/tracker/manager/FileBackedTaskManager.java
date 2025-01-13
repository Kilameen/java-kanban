package tracker.manager;

import tracker.exception.ManagerLoadException;
import tracker.exception.ManagerSaveException;
import tracker.tasks.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;
    private static final String FILE_TITLE = String.join(",", "id", "type", "name", "status", "description", "startTime", "duration", "epicID");
    private final DateTimeFormatter dateTimeFormatter = Task.dateTimeFormatter;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() {
        try (FileWriter fileWriter = new FileWriter(file, StandardCharsets.UTF_8);
             BufferedWriter writer = new BufferedWriter(fileWriter)) {
            writer.write(FILE_TITLE);
            writer.write("\n");

            for (Task task : getTasks()) {
                writer.write(taskToString(task));
                writer.write("\n");
            }

            for (Task epic : getEpics()) {
                writer.write(taskToString(epic));
                writer.write("\n");
            }

            for (Task subTask : getSubtasks()) {
                writer.write(taskToString(subTask));
                writer.write("\n");
            }

        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    private void load() {
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
        builder.append(task.getStartTime().format(dateTimeFormatter)).append(",");
        builder.append(task.getDuration().toMinutes()).append(",");
        if (task instanceof SubTask) {
            builder.append(((SubTask) task).getEpicID()).append(",");
        } else {
            builder.append(",");
        }
        return builder.toString();
    }

    private Task fromString(String line) {
        String[] parameters = line.split(",");
        int id = Integer.parseInt(parameters[0]);
        Type type = Type.valueOf(parameters[1]);
        String name = parameters[2];
        Status status = Status.valueOf(parameters[3]);
        String description = parameters[4];
        LocalDateTime startTime = LocalDateTime.parse(parameters[5], dateTimeFormatter);
        Long duration = Long.parseLong(parameters[6]);

        Task task;
        try {
            switch (type) {
                case TASK:
                    task = new Task(name, status, description, startTime, duration);
                    break;
                case EPIC:
                    task = new Epic(name, status, description, startTime, duration);
                    break;
                case SUBTASK:
                    task = new SubTask(name, status, description, startTime, duration, Integer.parseInt(parameters[7]));
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
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(SubTask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(SubTask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteAll() {
        super.deleteAll();
        save();
    }
}