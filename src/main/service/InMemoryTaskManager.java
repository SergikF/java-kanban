package main.service;

import main.classes.Epic;
import main.classes.Status;
import main.classes.SubTask;
import main.classes.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    protected int globalId = 1; // Глобальный счётчик всех задач
    protected final HashMap<Integer, Task> task = new HashMap<>();
    protected final HashMap<Integer, Epic> epic = new HashMap<>();
    protected final HashMap<Integer, SubTask> subTask = new HashMap<>();
    protected final HistoryManager history = Managers.getDefaultHistory();

    // добавляем задачу
    @Override
    public void addTask(Task taskNew) { //добавляем задачу
        taskNew.setId(globalId++);
        task.put(taskNew.getId(), taskNew);
    }

    // добавляем эпик
    @Override
    public void addEpic(Epic epicNew) { //добавляем эпик
        epicNew.setId(globalId++);
        epic.put(epicNew.getId(), epicNew);
    }

    // добавляем подзадачу в эпике, с проверкой, что эпик существует
    @Override
    public boolean addSubTask(SubTask subTaskNew) {
        if (epic.containsKey(subTaskNew.getIdEpic())) {
            subTaskNew.setId(globalId++);
            subTask.put(subTaskNew.getId(), subTaskNew);
            epic.get(subTaskNew.getIdEpic()).addSubTask(subTaskNew.getId());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getGlobalId() {
        return globalId;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> result = new ArrayList<>();
        for (int key : task.keySet()) {
            result.add(task.get(key));
        }
        return result;
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> result = new ArrayList<>();
        for (int key : epic.keySet()) {
            result.add(epic.get(key));
        }
        return result;
    }

    @Override
    public ArrayList<SubTask> getAllSubTasks() {
        ArrayList<SubTask> result = new ArrayList<>();
        for (int key : subTask.keySet()) {
            result.add(subTask.get(key));
        }
        return result;
    }

    @Override
    public Task getTask(int id) {
        if (task.containsKey(id)) {
            history.add(task.get(id));
        }
        return task.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        if (epic.containsKey(id)) {
            history.add(epic.get(id));
        }
        return epic.get(id);
    }

    @Override
    public SubTask getSubTask(int id) {
        if (subTask.containsKey(id)) {
            history.add(subTask.get(id));
        }
        return subTask.get(id);
    }

    // выводим информацию об эпике по его id, с проверкой, что эпик существует
    @Override
    public Object getEpicSubTasks(int id) {
        ArrayList<SubTask> result = new ArrayList<>();
        if (epic.containsKey(id)) {
            ArrayList<Integer> listTemp = epic.get(id).getIdSubTasks();
            for (Integer integer : listTemp) {
                result.add(subTask.get(integer));
                history.add(subTask.get(integer));
            }
            history.add(epic.get(id));
        } else {
            return false; // если эпик не существует
        }
        return result;
    }


    // удаление задач
    @Override
    public void deleteAllTasks() {
        for (int key : task.keySet()) {
            deleteTask(key);
        }
    }

    // удаление подзадач
    @Override
    public void deleteAllSubTasks() {
        for (int key : subTask.keySet()) {
            deleteSubTask(key);
        }
    }

    // удаление эпиков, вместе с их подзадачами, для безопасности данных
    @Override
    public void deleteAllEpics() {
        for (int key : epic.keySet()) {
            deleteEpic(key);
        }
    }

    // удаление задачи по id
    @Override
    public boolean deleteTask(int id) {
        if (task.containsKey(id)) { // если id найдено - удаляем задачу
            task.remove(id);
            history.remove(id);
            return true;
        } else { // если id не найдено - уведомляем об этом
            return false;
        }
    }

    // удаление эпика по id, вместе с его подзадачами для безопасности данных
    @Override
    public boolean deleteEpic(int id) {
        if (epic.containsKey(id)) { // если id найдено в - удаляем эпик
            ArrayList<Integer> listTemp = epic.get(id).getIdSubTasks();
            for (Integer integer : listTemp) { // разыскиваем подзадачи эпика и удаляем их
                subTask.remove(integer);
                history.remove(integer);
            }
            epic.remove(id);
            history.remove(id);
            return true;
        } else { // если id не найдено - уведомляем об этом
            return false;
        }
    }

    // удаление подзадачи по id, с удалением подзадачи в списке подзадач в эпике
    @Override
    public boolean deleteSubTask(int id) {
        if (subTask.containsKey(id)) { // если id найдено в подзадачах - удаляем подзадачу
            SubTask subTaskTemp = subTask.get(id);
            subTask.remove(id);
            history.remove(subTaskTemp.getId());
            // находим эпик этой подзадачи и в списке подзадач эпика - удаляем текущую подзадачу
            epic.get(subTaskTemp.getIdEpic()).getIdSubTasks().remove((Object) subTaskTemp.getId());
            checkStatusEpic(subTaskTemp.getIdEpic()); // проверяем статус эпика, чтобы откорректировать реальный статус
            return true;
        } else { // если id не найдено - уведомляем об этом
            return false;
        }
    }

    @Override
    public boolean updateTask(Task taskNew) {
        if (task.containsKey(taskNew.getId())) {
            // если id найдено в задачах - обновляем задачу
            task.put(taskNew.getId(), taskNew);
            return true;
        } else { // если id не найдено - возвращаем false
            return false;
        }
    }

    @Override
    public boolean updateEpic(Epic epicNew) {
        if (epic.containsKey(epicNew.getId())) {
            // если id найдено в эпиках - обновляем эпик
            epic.put(epicNew.getId(), epicNew);
            checkStatusEpic(epicNew.getId()); // поверяем статус эпика, чтобы откорректировать реальный статус
            // на тот случай, если во входном эпике пришёл статус какой-либо
            return true;
        } else { // если id не найдено - возвращаем false
            return false;
        }
    }

    @Override
    public boolean updateSubTask(SubTask subTaskNew) {
        if (subTask.containsKey(subTaskNew.getId())) {
            // если id найдено в подзадачах - обновляем подзадачу
            subTask.put(subTaskNew.getId(), subTaskNew);
            // после обновления подзадачи проводим проверку, как это повлияло на статус эпика этой задачи
            checkStatusEpic(subTaskNew.getIdEpic());
            return true;
        } else { // если id не найдено - возвращаем false
            return false;
        }
    }

    private void checkStatusEpic(int id) {
        int statusResult = 0; // временная переменная для подсчёта новых и выполненных задач в эпике
        ArrayList<Integer> listTemp = epic.get(id).getIdSubTasks(); // список подзадач для проверки статуса
        // перебираем задачи эпика для проверки их статуса состояния
        for (Integer integer : listTemp) {
            // если статус подзадачи NEW - уменьшаем на единицу
            if (subTask.get(integer).getStatus() == Status.NEW) {
                statusResult--;
            }
            // если статус подзадачи DONE - увеличиваем на единицу
            if (subTask.get(integer).getStatus() == Status.DONE) {
                statusResult++;
            }
        }
        if (statusResult == listTemp.size() && !listTemp.isEmpty()) {
            // если переменная равна количеству подзадач в непустом списке - значит эпик выполнен
            epic.get(id).setStatus(Status.DONE);
        } else if (statusResult == (-listTemp.size()) || listTemp.isEmpty()) {
            // если переменная равна количеству подзадач в списке, но с отрицательным знаком
            // или список подзадач пуст - значит эпик не начинал выполняться
            epic.get(id).setStatus(Status.NEW);
        } else {
            // все другие варианты означают, что эпик в процессе выполнения
            epic.get(id).setStatus(Status.IN_PROGRESS);
        }
    }

    public List<Task> getHistory() {
        return history.getHistory();
    }

    @Override
    public File getFile() {
        return null;
    }
}
