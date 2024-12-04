package main.service;

import main.classes.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;


public class FileBackedTaskManager extends InMemoryTaskManager {

    public Path path;

    public FileBackedTaskManager(Path path) {
        super();
        this.path = path;
        this.loadFromFile();
    }

    public Path getPath() {
        return path;
    }

    public void loadFromFile() {
        if(Files.exists(this.path)) {
            try (BufferedReader br = Files.newBufferedReader(this.path, StandardCharsets.UTF_8)) {
                // br.readLine();
                while (br.ready()) {
                    String line = br.readLine();
                    Task loadTask = fromString(line);

                    if (loadTask.getId() > globalId) { // если id больше чем globalId - присваиваем счётчику globalId значение id
                        globalId = loadTask.getId();
                    }

                    switch (loadTask.getClass().getSimpleName()) {
                        case "Task" -> task.put(loadTask.getId(), loadTask);
                        case "Epic" -> epic.put(loadTask.getId(), (Epic) loadTask);
                        case "SubTask" -> {
                            subTask.put(loadTask.getId(), (SubTask) loadTask);
                            epic.get(((SubTask) loadTask).getIdEpic()).addSubTask(loadTask.getId());
                        }
                    }
                    globalId++; // увеличиваем счётчик globalId
                }
            } catch (IOException e) {
                throw new ManagerSaveException("Не удалось восстановить данные из файла или указанный файл не найден");
            }
        }
    }

    public Task fromString(String value) { // метод обработки считываемой из файла строки
        String[] str = value.split(",");
        int id = Integer.parseInt(str[0]);
        TypeTasks type = TypeTasks.valueOf(str[1]);
        String name = str[2];
        Status status = Status.valueOf(str[3]);
        String description = str[4];
        int idEpic = Integer.parseInt(str[5]);
        switch (type) {
            case TASK -> {
                return new Task(id, name, description, status);
            }
            case EPIC -> {
                return new Epic(id, name, description, status, new ArrayList<>());
            }
            case SUBTASK -> {
                return new SubTask(id, name, description, status, idEpic);
            }
            default -> {
                return null;
            }
        }
    }

    public void save() {
        try (BufferedWriter bw = Files.newBufferedWriter(this.path, StandardCharsets.UTF_8)) {
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

    public String toString(Task task) { // метод обработки считываемой из файла строки
        String type ="";
        int idEpic = 0;
        switch (task.getClass().getSimpleName()) {
            case "Task" -> type = "TASK";
            case "Epic" -> type = "EPIC";
            case "SubTask" -> {
                type = "SUBTASK";
                idEpic = ((SubTask) task).getIdEpic();
            }
        }
        return (String) ( task.getId() + "," + type + "," + task.getName()+ "," + task.getStatus()+ "," + task.getDescription()+ "," + idEpic);
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
