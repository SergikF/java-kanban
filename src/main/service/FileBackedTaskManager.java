package main.service;

import main.classes.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        super();
        this.file = file;
        this.loadFromFile();
    }

    public File getFile() {
        return file;
    }

    private void loadFromFile() {

        // если файл существует, то считываем из него данные

        if (this.file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(this.file, StandardCharsets.UTF_8))) {
                while (br.ready()) {
                    String line = br.readLine();
                    Task loadTask = fromString(line);


                    // globalId++; // увеличиваем счётчик globalId
                    switch (loadTask.getClass().getSimpleName()) {
                        case "Task" -> addTask(loadTask);
                        case "Epic" -> addEpic((Epic) loadTask);
                        case "SubTask" -> {
                            addSubTask((SubTask) loadTask);
                        }
                    }
                    // если id больше чем globalId - присваиваем счётчику globalId значение id
                    if (loadTask.getId() >= globalId) {
                        globalId = loadTask.getId() + 1;
                    }
                }
            } catch (IOException e) {
                throw new ManagerSaveException("Не удалось восстановить данные из файла или указанный файл не найден");
            }
        }
    }

    private Task fromString(String value) { // метод обработки считываемой из файла строки
        String[] str = value.split(",");
        int id = Integer.parseInt(str[0]);
        TypeTasks type = TypeTasks.valueOf(str[1]);
        String name = str[2];
        Status status = Status.valueOf(str[3]);
        String description = str[4];
        int idEpic = Integer.parseInt(str[5]);
        // определяем - если было записано с данными времени старта и длительности -
        if (!str[6].equals("0")) {
            LocalDateTime startTime = LocalDateTime.parse(str[6],
                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
            Duration duration = Duration.ofMinutes(Integer.parseInt(str[7]));
            switch (type) {
                case TASK -> {
                    return new Task(id, name, description, status, startTime, duration);
                }
                case EPIC -> {
                    return new Epic(id, name, description, status, startTime, duration);
                }
                case SUBTASK -> {
                    return new SubTask(id, name, description, status, idEpic, startTime, duration);
                }
                default -> {
                    return null;
                }
            }
        } else { // если временных данных не было - используем упрощённый конструктор
            switch (type) {
                case TASK -> {
                    return new Task(id, name, description, status);
                }
                case EPIC -> {
                    return new Epic(id, name, description, status);
                }
                case SUBTASK -> {
                    return new SubTask(id, name, description, status, idEpic);
                }
                default -> {
                    return null;
                }
            }

        }
    }

    private void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.file, StandardCharsets.UTF_8))) {
            // Записываем в файл Task
            for (int key : task.keySet()) {
                bw.write(toString(task.get(key)));
                bw.newLine();
            }
            // Записываем в файл Epic
            for (int key : epic.keySet()) {
                bw.write(toString(epic.get(key)));
                bw.newLine();
            }
            // Записываем в файл SubTask
            for (int key : subTask.keySet()) {
                bw.write(toString(subTask.get(key)));
                bw.newLine();
            }
            bw.flush();
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось записать данные в файл");
        }
    }

    private String toString(Task task) { // метод формирования строки для файла
        String type = "";
        int idEpic = 0;
        switch (task.getClass().getSimpleName()) {
            case "Task" -> type = "TASK";
            case "Epic" -> type = "EPIC";
            case "SubTask" -> {
                type = "SUBTASK";
                idEpic = ((SubTask) task).getIdEpic();
            }
        }
        return task.getId() + "," + type + "," + task.getName() + "," + task.getStatus() + ","
                + task.getDescription() + "," + idEpic + ","
                + (task.getStartTime() == null ? "0" : task.getStartTime()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
                + "," + (task.getDurationTask() == null ? "0" : task.getDurationTask().toMinutes());
    }

    // переопределение методов для сохранения данных в файле

    @Override
    public boolean updateSubTask(SubTask subTaskNew) {
        if (super.updateSubTask(subTaskNew)) {
            save();
            return true;
        }
        return false;
    }

    @Override
    public boolean updateEpic(Epic epicNew) {
        if (super.updateEpic(epicNew)) {
            save();
            return true;
        }
        return false;
    }

    @Override
    public boolean updateTask(Task taskNew) {
        if (super.updateTask(taskNew)) {
            save();
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteSubTask(int id) {
        if (super.deleteSubTask(id)) {
            save();
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteEpic(int id) {
        if (super.deleteEpic(id)) {
            save();
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteTask(int id) {
        if (super.deleteTask(id)) {
            save();
            return true;
        }
        return false;
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public boolean addSubTask(SubTask subTaskNew) {
        if (super.addSubTask(subTaskNew)) {
            save();
            return true;
        }
        return false;
    }

    @Override
    public void addEpic(Epic epicNew) {
        super.addEpic(epicNew);
        save();
    }

    @Override
    public void addTask(Task taskNew) {
        super.addTask(taskNew);
        save();
    }
}
