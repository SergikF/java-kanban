import java.util.Objects;

// Базовый класс задачи

public class Task {
    public static int globalIdTask = 1; // Глобальный счётчик всех задач
    public int idTask;
    public String nameTask;
    public String descriptionTask;
    public StatusTask statusTask = StatusTask.NEW;

    public Task(String nameTask, String descriptionTask) { // Конструктор для создания задачи
        this.idTask = globalIdTask;
        this.nameTask = nameTask;
        this.descriptionTask = descriptionTask;
    }

    // Конструктор для создания объектов, необходимых при обновлении данных в задачах
    public Task(int idTask, String nameTask, String descriptionTask, StatusTask statusTask) {
        this.idTask = idTask;
        this.nameTask = nameTask;
        this.descriptionTask = descriptionTask;
        this.statusTask = statusTask;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return idTask == task.idTask && Objects.equals(nameTask, task.nameTask) && Objects.equals(descriptionTask, task.descriptionTask) && statusTask == task.statusTask;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTask, nameTask, descriptionTask, statusTask);
    }

    @Override
    public String toString() {
        return "Task{" +
                "idTask=" + idTask +
                ", nameTask='" + nameTask + '\'' +
                ", descriptionTask='" + descriptionTask + '\'' +
                ", statusTask=" + statusTask +
                '}';
    }

}
