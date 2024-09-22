// Класс управления проектом

import java.util.ArrayList;
import java.util.HashMap;

/*
 Заметка о возвращаемых булевых значениях в методах.
 По той причине, что вывод в консоль не предусмотрен - из методов убрано оповещение в консоль
 о том, что задача не найдена. Но так как, возможно в будущем, понадобится возвращать
 информацию в случае неуспеха пока сделал функции с возвратом булевого значения.
 Если в следующем развитии проекта это не понадобиться - тогда уберу.
*/

public class TaskManager {
    private int globalId = 1; // Глобальный счётчик всех задач
    private final HashMap<Integer, Task> task = new HashMap<>();
    private final HashMap<Integer, Epic> epic = new HashMap<>();
    private final HashMap<Integer, SubTask> subTask = new HashMap<>();

    // добавляем задачу
    public void addTask(Task taskNew) { //добавляем задачу
            taskNew.setId(globalId++);
            task.put(taskNew.getId(), taskNew);
    }

    // добавляем эпик
    public void addEpic(Epic epicNew) { //добавляем эпик
            epicNew.setId(globalId++);
            epic.put(epicNew.getId(), epicNew);
    }

    // добавляем подзадачу в эпике, с проверкой, что эпик существует
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

    public int getGlobalId() {
        return globalId;
    }

    public HashMap<Integer, Task> getAllTasks() {
        return task;
    }

    public HashMap<Integer, Epic> getAllEpics() {
        return epic;
    }

    public HashMap<Integer, SubTask> getAllSubTasks() {
        return subTask;
    }

    public Task getTask(int id) {
        return task.get(id);
    }

    public Epic getEpic(int id) {
        return epic.get(id);
    }

    public SubTask getSubTask(int id) {
        return subTask.get(id);
    }

    // выводим информацию об эпике по его id, с проверкой, что эпик существует
    public Object getEpicSubTasks(int id) {
        ArrayList<SubTask> result = new ArrayList<>();
        if (epic.containsKey(id)) {
            ArrayList<Integer> listTemp = epic.get(id).getIdSubTasks();
            for (Integer integer : listTemp) {
                result.add(subTask.get(integer));
            }
        } else {
            return false; // если эпик не существует
        }
        return result;
    }


    // удаление задач
    public void deleteAllTasks() {
        task.clear();
    }

    // удаление подзадач
    public void deleteAllSubTasks() {
        subTask.clear();
        for (int key : epic.keySet()) { // удаляем все подзадачи эпика и обновляем статус эпика (NEW)
            epic.get(key).getIdSubTasks().clear();
            checkStatusEpic(key);
        }
    }

    // удаление эпиков, вместе с их подзадачами, для безопасности данных
    public void deleteAllEpics() {
        epic.clear();
        subTask.clear();
    }

    // удаление задачи по id
    public boolean deleteTask(int id) {
        if (task.containsKey(id)) { // если id найдено - удаляем задачу
            task.remove(id);
            return true;
        } else { // если id не найдено - уведомляем об этом
            return false;
        }
    }

    // удаление эпика по id, вместе с его подзадачами для безопасности данных
    public boolean deleteEpic(int id) {
        if (epic.containsKey(id)) { // если id найдено в - удаляем эпик
            ArrayList<Integer> listTemp = epic.get(id).getIdSubTasks();
            for (Integer integer : listTemp) { // разыскиваем подзадачи эпика и удаляем их
                subTask.remove(integer);
            }
            epic.remove(id);
            return true;
        } else { // если id не найдено - уведомляем об этом
            return false;
        }
    }

    // удаление подзадачи по id, с удалением подзадачи в списке подзадач в эпике
    public boolean deleteSubTask(int id) {
        if (subTask.containsKey(id)) { // если id найдено в подзадачах - удаляем подзадачу
            SubTask subTaskTemp = subTask.get(id);
            subTask.remove(id);
            // находим эпик этой подзадачи и в списке подзадач эпика - удаляем текущую подзадачу
            epic.get(subTaskTemp.getIdEpic()).getIdSubTasks().remove((Object)subTaskTemp.getId());
            checkStatusEpic(subTaskTemp.getIdEpic()); // проверяем статус эпика, чтобы откорректировать реальный статус
            return true;
        } else { // если id не найдено - уведомляем об этом
            return false;
        }
    }

    public boolean updateTask(Task taskNew) {
        if (task.containsKey(taskNew.getId())) {
            // если id найдено в задачах - обновляем задачу
            task.put(taskNew.getId(), taskNew);
            return true;
        } else { // если id не найдено - возвращаем false
            return false;
        }
    }

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

    public boolean checkStatusEpic(int id) {
        if (epic.containsKey(id)) {
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
        } else { // если id найдено - возвращаем false
            return false;
        }
        return true;
    }
}
