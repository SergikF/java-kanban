import java.util.ArrayList;
import java.util.Objects;

// Класс Эпиков

public class Epic extends Task{

    public ArrayList<Integer> subTasks; // Массив для хранения id подзадач

    public Epic(String name, String description) {
        super(name, description);
        subTasks = new ArrayList<>();
    }

    public void addSubTask(int idTask) {
        subTasks.add(idTask);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subTasks, epic.subTasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTasks);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "idTask=" + idTask +
                ", nameTask='" + nameTask + '\'' +
                ", descriptionTask='" + descriptionTask + '\'' +
                ", subTasks=" + subTasks +
                ", statusTask=" + statusTask +
                '}';
    }

}
