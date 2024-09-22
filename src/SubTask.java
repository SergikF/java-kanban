import java.util.Objects;

// Класс подзадач эпиков

public class SubTask extends Task {
    private final int idEpic; // Идентификатор родительского эпика

    public SubTask(int id, String name, String description, Status status, int idEpic) {
        super(id, name, description, status);
        this.idEpic = idEpic;
    }

    public int getIdEpic() {
        return idEpic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return idEpic == subTask.idEpic;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), idEpic);
    }

    @Override
    public String toString() {
        return "Sub" + super.toString().substring(0, super.toString().length() - 1) + ", idEpic=" + idEpic + "}";
    }
}