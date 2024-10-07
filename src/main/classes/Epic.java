package main.classes;

import java.util.ArrayList;
import java.util.Objects;

// Класс Эпиков

public class Epic extends Task {

    private final ArrayList<Integer> idSubTasks; // Массив для хранения id подзадач

    public Epic(int id, String name, String description, Status status, ArrayList<Integer> idSubTasks) {
        super(id, name, description, status);
        this.idSubTasks = new ArrayList<>();
        this.idSubTasks.addAll(idSubTasks);
    }

    public void addSubTask(int idTask) {
        idSubTasks.add(idTask);
    }

    public ArrayList<Integer> getIdSubTasks() {
        return idSubTasks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(idSubTasks, epic.idSubTasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), idSubTasks);
    }

    @Override
    public String toString() {
        return "Epic{" + (super.toString()).substring(5, super.toString().length() - 1) + ", idSubTasks=" + idSubTasks + "}";
    }

}
