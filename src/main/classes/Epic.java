package main.classes;

import java.time.Duration;
import java.time.LocalDateTime;

// Класс Эпиков

public class Epic extends Task {

    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status);
    }

    public Epic(int id, String name, String description, Status status, LocalDateTime startTime, Duration durationTask) {
        super(id, name, description, status, startTime, durationTask);
    }

    @Override
    public String toString() {
        return "Epic{" + (super.toString()).substring(5, super.toString().length() - 1) + "}";
    }

}
