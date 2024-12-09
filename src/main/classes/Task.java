package main.classes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

// Базовый класс задачи

public class Task {
    private int id;
    private String name;
    private String description;
    private Status status;
    private LocalDateTime startTime;
    private Duration durationTask;
    private LocalDateTime endTime;

    public Task(int id, String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(int id, String name, String description, Status status, LocalDateTime startTime, Duration durationTask) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.durationTask = durationTask;
        this.endTime = (durationTask == null ? startTime : startTime.plus(durationTask));
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDurationTask() {
        return durationTask;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public void setDurationTask(Duration durationTask) { this.durationTask = durationTask; }

    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Task task)) return false;
        return id == task.id && Objects.equals(name, task.name)
                && Objects.equals(description, task.description) && status == task.status
                && Objects.equals(startTime, task.startTime)
                && Objects.equals(durationTask, task.durationTask)
                && Objects.equals(endTime, task.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status, startTime, durationTask, endTime);
    }

    @Override
    public String toString() {
        return "Task{" + "id=" + id + ", name='" + name + '\'' + ", description='" + description + '\'' + ", status=" + status
               + ", startTime=" + startTime + ", durationTask=" + durationTask + '}';
    }
}
