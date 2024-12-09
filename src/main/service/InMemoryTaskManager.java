package main.service;

import main.classes.Epic;
import main.classes.Status;
import main.classes.SubTask;
import main.classes.Task;

import java.io.File;
import java.time.Duration;
import java.util.*;


public class InMemoryTaskManager implements TaskManager {
    protected int globalId = 1; // Глобальный счётчик всех задач
    protected final HashMap<Integer, Task> task = new HashMap<>();
    protected final HashMap<Integer, Epic> epic = new HashMap<>();
    protected final HashMap<Integer, SubTask> subTask = new HashMap<>();
    protected final TreeSet<Task> prioritizedTasks = new TreeSet<Task>(new Comparator<Task>() {
        public int compare(Task o1, Task o2) {
            if (o1 == null) return 1;
            if (o2 == null) return -1;
            return o1.getStartTime().compareTo(o2.getStartTime());
        }
    });
    protected final HistoryManager history = Managers.getDefaultHistory();

    // добавляем задачу
    @Override
    public void addTask(Task taskNew) { //добавляем задачу
        task.put(taskNew.getId(), taskNew);
        isOverlay(taskNew);
        setPrioritizedTasks(taskNew);
    }

    // добавляем эпик
    @Override
    public void addEpic(Epic epicNew) { //добавляем эпик
        epic.put(epicNew.getId(), epicNew);
    }

    // добавляем подзадачу в эпике, с проверкой, что эпик существует
    @Override
    public boolean addSubTask(SubTask subTaskNew) {
        if (epic.containsKey(subTaskNew.getIdEpic())) {
            subTask.put(subTaskNew.getId(), subTaskNew);
            isOverlay(subTaskNew);
            setPrioritizedTasks(subTaskNew);
            checkStatusEpic(subTaskNew.getIdEpic());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getGlobalId() {
        return globalId++;
    }

    @Override
    public List<Task> getAllTasks() {
        // выводим информацию об задачах при помощи stream
        return task.values().stream()
                .sorted(((c1, c2) -> {
                    if (c1.getStartTime() == null || c2.getStartTime() == null) return 0;
                    return c1.getStartTime().compareTo(c2.getStartTime());
                }))
                .toList();
    }

    @Override
    public List<Epic> getAllEpics() {
        // выводим информацию об эпиках при помощи stream
        return epic.values().stream()
                .sorted(((c1, c2) -> {
                    if (c1.getStartTime() == null || c2.getStartTime() == null) return 0;
                    return c1.getStartTime().compareTo(c2.getStartTime());
                }))
                .toList();
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        // выводим информацию об SubTasks при помощи stream
        return subTask.values().stream()
                .sorted(((c1, c2) -> {
                    if (c1.getStartTime() == null || c2.getStartTime() == null) return 0;
                    return c1.getStartTime().compareTo(c2.getStartTime());
                }))
                .toList();
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
    public List<SubTask> getEpicSubTasks(int id) {
        if (epic.containsKey(id)) {
            // выводим информацию об эпике при помощи stream
            history.add(epic.get(id));
            return subTask.values().stream()
                    .filter((subTask -> subTask.getIdEpic() == id))
                    .sorted(((c1, c2) -> {
                        if (c1.getStartTime() == null || c2.getStartTime() == null) return 0;
                        return c1.getStartTime().compareTo(c2.getStartTime());
                    }))
                    .toList();
        } else {
            return null; // если эпик не существует
        }
    }


    // удаление задач
    @Override
    public void deleteAllTasks() {
        for (int key : task.keySet().stream().toList()) {
            deleteTask(key);
        }
    }

    // удаление подзадач
    @Override
    public void deleteAllSubTasks() {
        for (int key : subTask.keySet().stream().toList()) {
            deleteSubTask(key);
        }
    }

    // удаление эпиков, вместе с их подзадачами, для безопасности данных
    @Override
    public void deleteAllEpics() {
        for (int key : epic.keySet().stream().toList()) {
            deleteEpic(key);
        }
    }

    // удаление задачи по id
    @Override
    public boolean deleteTask(int id) {
        if (task.containsKey(id)) { // если id найдено - удаляем задачу
            task.remove(id);
            history.remove(id);
            prioritizedTasks.remove(task.get(id));
            return true;
        } else { // если id не найдено - уведомляем об этом
            return false;
        }
    }

    // удаление эпика по id, вместе с его подзадачами для безопасности данных
    @Override
    public boolean deleteEpic(int id) {
        if (epic.containsKey(id)) { // если id найдено в - удаляем эпик
            for (SubTask idEpic : getEpicSubTasks(id)) {
                subTask.remove(idEpic.getId());
                history.remove(idEpic.getId());
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
            int idEpic = subTask.get(id).getIdEpic();
            subTask.remove(id);
            history.remove(id);
            prioritizedTasks.remove(subTask.get(id));
            checkStatusEpic(idEpic); // проверяем статус эпика, чтобы откорректировать реальный статус
            return true;
        } else { // если id не найдено - уведомляем об этом
            return false;
        }
    }

    @Override
    public boolean updateTask(Task taskNew) {
        if (task.containsKey(taskNew.getId())) {
            // если id найдено в задачах - обновляем задачу
            task.put(taskNew.getId(), new Task(taskNew.getId(),
                    (taskNew.getName() == null ? task.get(taskNew.getId()).getName() : taskNew.getName()),
                    (taskNew.getDescription() == null ? task.get(taskNew.getId()).getDescription() : taskNew.getDescription()),
                    (taskNew.getStatus() == null ? task.get(taskNew.getId()).getStatus() : taskNew.getStatus()),
                    (taskNew.getStartTime() == null ? task.get(taskNew.getId()).getStartTime() : taskNew.getStartTime()),
                    (taskNew.getDurationTask() == null ? task.get(taskNew.getId()).getDurationTask() : taskNew.getDurationTask())));
            isOverlay(taskNew);
            setPrioritizedTasks(task.get(taskNew.getId()));
            return true;
        } else { // если id не найдено - возвращаем false
            return false;
        }
    }

    @Override
    public boolean updateEpic(Epic epicNew) {
        if (epic.containsKey(epicNew.getId())) {
            // если id найдено в эпиках - обновляем эпик
            epic.put(epicNew.getId(), new Epic(epicNew.getId(),
                    (epicNew.getName() == null ? epic.get(epicNew.getId()).getName() : epicNew.getName()),
                    (epicNew.getDescription() == null ? epic.get(epicNew.getId()).getDescription() : epicNew.getDescription()),
                    (epicNew.getStatus() == null ? epic.get(epicNew.getId()).getStatus() : epicNew.getStatus()),
                    (epicNew.getStartTime() == null ? epic.get(epicNew.getId()).getStartTime() : epicNew.getStartTime()),
                    (epicNew.getDurationTask() == null ? epic.get(epicNew.getId()).getDurationTask() : epicNew.getDurationTask())));
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
            subTask.put(subTaskNew.getId(), new SubTask(subTaskNew.getId(),
                    (subTaskNew.getName() == null ? subTask.get(subTaskNew.getId()).getName() : subTaskNew.getName()),
                    (subTaskNew.getDescription() == null ? subTask.get(subTaskNew.getId()).getDescription() : subTaskNew.getDescription()),
                    (subTaskNew.getStatus() == null ? subTask.get(subTaskNew.getId()).getStatus() : subTaskNew.getStatus()),
                    (subTaskNew.getIdEpic() == 0 ? subTask.get(subTaskNew.getId()).getIdEpic() : subTaskNew.getIdEpic()),
                    (subTaskNew.getStartTime() == null ? subTask.get(subTaskNew.getId()).getStartTime() : subTaskNew.getStartTime()),
                    (subTaskNew.getDurationTask() == null ? subTask.get(subTaskNew.getId()).getDurationTask() : subTaskNew.getDurationTask())));
            // после обновления подзадачи проводим проверку, как это повлияло на статус эпика этой задачи
            isOverlay(subTaskNew);
            setPrioritizedTasks(subTask.get(subTaskNew.getId()));
            checkStatusEpic(subTaskNew.getIdEpic());
            return true;
        } else { // если id не найдено - возвращаем false
            return false;
        }
    }

    private void checkStatusEpic(int id) {
        int statusResult = 0; // временная переменная для подсчёта новых и выполненных задач в эпике
        List<SubTask> listTemp = getEpicSubTasks(id); // список подзадач для проверки статуса
        // перебираем задачи эпика для проверки их статуса состояния
        for (SubTask status : listTemp) {
            // если статус подзадачи NEW - уменьшаем на единицу
            if (status.getStatus() == Status.NEW) {
                statusResult--;
            }
            // если статус подзадачи DONE - увеличиваем на единицу
            if (status.getStatus() == Status.DONE) {
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
        // теперь надо высчитать Время начала эпика и время конца эпика
        // начало эпика = Время самой ранней задачи
        // конец эпика = Время самой поздней задачи
        List<SubTask> listNotNull = listTemp.stream()
                .map(c -> {
                    if (c.getStartTime() != null) {
                        return c;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();
        if (!listNotNull.isEmpty()) {
            epic.get(id).setStartTime(listNotNull.getFirst().getStartTime());
            epic.get(id).setDurationTask(Duration.between(listNotNull.getFirst().getStartTime(), listNotNull.getLast().getEndTime()));
            epic.get(id).setEndTime(listNotNull.getLast().getEndTime());
        }
    }

    public List<Task> getHistory() {
        return history.getHistory();
    }

    @Override
    public File getFile() {
        return null;
    }

    public void setPrioritizedTasks(Task task) {
        if (task.getStartTime() != null) prioritizedTasks.add(task);
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    @Override
    public boolean isOverlay(Task checkTask) {
        // проверить пересекается ли эта задача по времени с другими задачами
        boolean ll = false;
        if (checkTask.getStartTime() != null && !getPrioritizedTasks().isEmpty()) {
        ll = getPrioritizedTasks().stream()
                .anyMatch( c->
                    (checkTask.getStartTime().isAfter(c.getStartTime())
                        && checkTask.getStartTime().isBefore(c.getEndTime())) ||
                    (checkTask.getEndTime().isAfter(c.getStartTime())
                        && checkTask.getEndTime().isBefore(c.getEndTime())) ||
                    (checkTask.getStartTime().isBefore(c.getStartTime())
                        && checkTask.getEndTime().isAfter(c.getEndTime()))
                );
        }
        System.out.println(ll+" пересечение - id: " + checkTask.getId() + " ! ");
        return ll;
    }
}
