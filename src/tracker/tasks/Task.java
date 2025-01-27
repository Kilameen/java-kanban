package tracker.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected Status status;
    protected Duration duration;// продолжительность задачи в минутах
    protected LocalDateTime startTime; // дата и время старта выполнения задачи
    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Task(String name, String description, Status status, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, Status status, String description, LocalDateTime startTime, Long durationMinutes) {
        this.name = name;
        this.status = status;
        this.description = description;
        this.startTime = startTime;
        this.duration = Duration.ofMinutes(durationMinutes);
    }

    public Task(int id, String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(int id ,String name, Status status, String description, LocalDateTime startTime, Long durationMinutes) {
        this.id=id;
        this.name = name;
        this.status = status;
        this.description = description;
        this.startTime = startTime;
        this.duration = Duration.ofMinutes(durationMinutes);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Task setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getEndTime() {
        if (getStartTime() == null) {
            return null;
        }
        return startTime.plus(duration);
    }

    public Task setDuration(Duration duration) {
        this.duration = duration;
        return this;
    }

    public Type getType() {
        return Type.TASK;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) &&
                Objects.equals(id, task.id) &&
                Objects.equals(status, task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }

    @Override
    public String toString() {
        String start = "";
        if (getStartTime() != null) {
            start = getStartTime().format(dateTimeFormatter);
        }

        return "model.Task.Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startTime=" + start +
                ", duration=" + duration.toMinutes() +
                '}';
    }
}