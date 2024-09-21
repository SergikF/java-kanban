// Класс управления проектом

public class Manager extends Base {
    static int counter; // временная переменная - счетчик задач при выводе

    // добавляем задачу
    public static void addTask(Task taskNew) { //добавляем задачу
        task.put(taskNew.idTask, taskNew);
        Task.globalIdTask++;
    }

    // добавляем эпик
    public static void addEpic(Task taskNew) { //добавляем эпик
        epic.put(taskNew.idTask, new Epic(taskNew.nameTask, taskNew.descriptionTask));
        Task.globalIdTask++;
    }

    // добавляем подзадачу в эпик, с проверкой, что эпик существует
    public static void addSubTask(Task taskNew, int idEpic) {//добавляем подзадачу
        if (epic.containsKey(idEpic)) {
            subTask.put(taskNew.idTask, new SubTask(taskNew.nameTask, taskNew.descriptionTask, idEpic));
            epic.get(idEpic).addSubTask(taskNew.idTask);
            Task.globalIdTask++;
        } else {
            System.out.println("Эпика с таким id не существует");
        }
    }

    // выводим список задач
    public static void printTasks() {
        System.out.println("Все задачи:");
        if (task.isEmpty() && epic.isEmpty() && subTask.isEmpty()) {
            System.out.println("Нет задач !");
        } else {
            counter = 1;
            for (int id : Base.task.keySet()) {
                Task task = Base.task.get(id);
                System.out.printf("%3d. ", counter++);
                System.out.println("Задача: " + task.nameTask + " [id:" + task.idTask + "]" +
                        " [Статус:" + task.statusTask + "]" + "\n" +
                        "     Описание: " + task.descriptionTask);
            }
        }
    }

    // выводим список эпиков
    public static void printEpics() {
        System.out.println("Все эпики:");
        if (task.isEmpty() && epic.isEmpty() && subTask.isEmpty()) {
            System.out.println("Нет эпиков !");
        } else {
            counter = 1;
            for (int id : Base.epic.keySet()) {
                Epic epic = Base.epic.get(id);
                System.out.printf("%3d. ", counter++);
                String result = "Эпик: " + epic.nameTask + " [id:" + epic.idTask + "]" +
                        " [Статус:" + epic.statusTask + "]" + "\n" +
                        "     Описание: " + epic.descriptionTask + "\n" +
                        "       Подзадачи: ";
                for (int i = 0; i < epic.subTasks.size(); i++) {
                    result +=  Base.subTask.get(epic.subTasks.get(i)).nameTask;
                    if(i!= epic.subTasks.size() - 1) {
                        result += ", ";
                    }
                }
                System.out.println(result);
            }
        }
    }

    // выводим список подзадач
    public static void printSubTasks() {
        System.out.println("Все подзадачи:");
        if (task.isEmpty() && epic.isEmpty() && subTask.isEmpty()) {
            System.out.println("Нет подзадач !");
        } else {
            counter = 1;
            for (int id : Base.subTask.keySet()) {
                SubTask subTask = Base.subTask.get(id);
                System.out.printf("%3d. ", counter++);
                String result = "Подзадача: " + subTask.nameTask + " [id:" + subTask.idTask + "]" +
                        " [Эпик:" + Base.epic.get(subTask.idEpic).nameTask + "]" +
                        " [Статус:" + subTask.statusTask + "]" + "\n" +
                        "     Описание: " + subTask.descriptionTask;
                System.out.println(result);
            }
        }
    }

    // выводим полную информацию об эпике по его id, с проверкой, что эпик существует
    public static void printEpic( int id) {
        if (epic.containsKey(id)) {
            Epic epic = Base.epic.get(id);
            System.out.println("Эпик: " + epic.nameTask + " [id:" + epic.idTask + "]" +
                    " [Статус:" + epic.statusTask + "]" + "\n" +
                    "  Описание: " + epic.descriptionTask + "\n" +
                    "    Подзадачи: ");
            counter = 1;
            for (int i = 0; i < epic.subTasks.size(); i++) {
                System.out.printf("    %3d. ", counter++);
                System.out.println(Base.subTask.get(epic.subTasks.get(i)).nameTask +
                        " [id:" + Base.subTask.get(epic.subTasks.get(i)).idTask + "]" +
                        " [Статус:" + Base.subTask.get(epic.subTasks.get(i)).statusTask + "]");
                System.out.println("      " + Base.subTask.get(epic.subTasks.get(i)).descriptionTask);
            }
        } else {
            System.out.println("Эпик с таким id не существует!");
        }
    }

    // выводим все данные канбана
    public static void printAllTasks() { //выводит все задачи
        System.out.println("Все данные канбана:");
        printTasks();
        printEpics();
        printSubTasks();
    }

    // удаление задач
    public static void deleteAllTask() {
        task.clear();
        System.out.println("Все задачи удалены!");
    }

    // удаление подзадач
    public static void deleteAllSubTask() {
        subTask.clear();
        System.out.println("Все подзадачи удалены!");
    }

    // удаление эпиков, вместе с их подзадачами, для безопасности данных
    public static void deleteAllEpic() {
        epic.clear();
        subTask.clear();
        System.out.println("Все эпики c их подзадачами удалены!");
    }

    // удаление любого типа задачи по его id
    public static void deleteSomething(int id) {
        String tempName;
        if (task.containsKey(id)) { // если id найдено в задачах - удаляем задачу
            tempName = task.get(id).nameTask;
            task.remove(id);
            System.out.println("Задача " + tempName + " [id:" + id + "] удалена!");
        } else if (epic.containsKey(id)) { // если id найдено в эпиках - удаляем эпик
            tempName = epic.get(id).nameTask;
            Epic epicTemp = Base.epic.get(id);
            for (int i = 0; i < epicTemp.subTasks.size(); i++) { // разыскиваем подзадачи эпика и удаляем их
                subTask.remove(epicTemp.subTasks.get(i));
            }
            epic.remove(id);
            System.out.println("Эпик " + tempName + " [id:" + id + "] и его подзадачи удалены!");
        } else if (subTask.containsKey(id)) { // если id найдено в подзадачах - удаляем подзадачу
            tempName = subTask.get(id).nameTask;
            // находим эпик этой подзадачи и в списке подзадач эпика - удаляем текущую подзадачу
            epic.get(subTask.get(id).idEpic).subTasks.remove((Object)subTask.get(id).idTask);
            subTask.remove(id);
            System.out.println("Подзадача " + tempName + " [id:" + id + "] удалена!");
        } else { // если id найдено - уведомляем об этом
            System.out.println("Никакого типа задач с id " + id + " не существует!");
        }
    }

    // получаем объекты по id
    public static Task getTask(int id) {
        Task resultTask;
        if (task.containsKey(id)) {
            resultTask = task.get(id);
        } else if (epic.containsKey(id)) {
            resultTask = epic.get(id);
        } else if (subTask.containsKey(id)) {
            resultTask = subTask.get(id);
        } else {
            return null; // если id найдено - возвращаем null
        }
        return resultTask;
    }

    public static void updateTask(Task taskNew) {
        if (task.containsKey(taskNew.idTask)) {
            // если id найдено в задачах - обновляем задачу
            // при этом проверяем содержимое переданного объекта - и обрабатываем только то, что нужно
            if (taskNew.nameTask != null && !taskNew.nameTask.isEmpty()) {
                task.get(taskNew.idTask).nameTask = taskNew.nameTask;
            }
            if (taskNew.descriptionTask != null && !taskNew.descriptionTask.isEmpty()) {
                task.get(taskNew.idTask).descriptionTask = taskNew.descriptionTask;
            }
            if (taskNew.statusTask != null) {
                task.get(taskNew.idTask).statusTask = taskNew.statusTask;
            }
        } else if (epic.containsKey(taskNew.idTask)) {
            // если id найдено в эпиках - обновляем эпик, но для эпика доступно только обновление описания эпика
            if (taskNew.nameTask != null && !taskNew.nameTask.isEmpty()) {
                epic.get(taskNew.idTask).nameTask = taskNew.nameTask;
            }
            if (taskNew.descriptionTask != null && !taskNew.descriptionTask.isEmpty()) {
                epic.get(taskNew.idTask).descriptionTask = taskNew.descriptionTask;
            }
        } else if (subTask.containsKey(taskNew.idTask)) {
            // если id найдено в подзадачах - обновляем подзадачу
            // при этом проверяем содержимое переданного объекта - и обрабатываем только то, что нужно
            if (taskNew.nameTask != null && !taskNew.nameTask.isEmpty()) {
                subTask.get(taskNew.idTask).nameTask = taskNew.nameTask;
            }
            if (taskNew.descriptionTask != null && !taskNew.descriptionTask.isEmpty()) {
                subTask.get(taskNew.idTask).descriptionTask = taskNew.descriptionTask;
            }
            if (taskNew.statusTask != null ) {
                subTask.get(taskNew.idTask).statusTask = taskNew.statusTask;
            }
            // после обновления подзадачи проводим проверку, как это повлияло на эпик этой задачи
            Epic epic = Base.epic.get(subTask.get(taskNew.idTask).idEpic);
            int statusResult = 0; // временная переменная для подсчёта новых и выполненных задач в эпике
            // перебираем задачи эпика для проверки их статуса состояния
            for (int i = 0; i < epic.subTasks.size(); i++) {
                // если статус подзадачи NEW - уменьшаем на единицу
                if(Base.subTask.get(epic.subTasks.get(i)).statusTask == StatusTask.NEW){
                    statusResult--;
                }
                // если статус подзадачи DONE - увеличиваем на единицу
                if(Base.subTask.get(epic.subTasks.get(i)).statusTask == StatusTask.DONE){
                    statusResult++;
                }
            }
            if (statusResult == epic.subTasks.size()) {
                // если переменная равна количеству задач в списке - значит все задачи выполнены
                epic.statusTask = StatusTask.DONE;
            } else if (statusResult == (-epic.subTasks.size())){
                // если переменная равна количеству задач в списке, но с отрицательным знаком - значит все задачи новые
                epic.statusTask = StatusTask.NEW;
            } else {
                // все другие варианты означают, что эпик в процессе выполнения
                epic.statusTask = StatusTask.IN_PROGRESS;
            }
        } else { // если id найдено - уведомляем об этом
            System.out.println("Задача с таким id не найдена!");
        }
    }
}
