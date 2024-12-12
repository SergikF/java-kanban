package main.service;

import main.classes.Epic;
import main.classes.SubTask;
import main.classes.Task;

import java.io.File;
import java.util.List;
import java.util.TreeSet;

public interface TaskManager {
    // добавляем задачу
    void addTask(Task taskNew);

    // добавляем эпик
    void addEpic(Epic epicNew);

    // добавляем подзадачу в эпике, с проверкой, что эпик существует
    boolean addSubTask(SubTask subTaskNew);

    int getGlobalId();

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<SubTask> getAllSubTasks();

    Task getTask(int id);

    Epic getEpic(int id);

    SubTask getSubTask(int id);

    // выводим информацию об эпике по его id, с проверкой, что эпик существует
    List<SubTask> getEpicSubTasks(int id);

    // удаление задач
    void deleteAllTasks();

    // удаление подзадач
    void deleteAllSubTasks();

    // удаление эпиков, вместе с их подзадачами, для безопасности данных
    void deleteAllEpics();

    // удаление задачи по id
    boolean deleteTask(int id);

    // удаление эпика по id, вместе с его подзадачами для безопасности данных
    boolean deleteEpic(int id);

    // удаление подзадачи по id, с удалением подзадачи в списке подзадач в эпике
    boolean deleteSubTask(int id);

    boolean updateTask(Task taskNew);

    boolean updateEpic(Epic epicNew);

    boolean updateSubTask(SubTask subTaskNew);

    List<Task> getHistory();

    File getFile();

    TreeSet<Task> getPrioritizedTasks();

    boolean isOverlay(Task task);
}
