public class Manager extends Base {
    static int counter;

    public static void addTask(Task taskNew) { //добавляем задачу
        task.put(taskNew.idTask, taskNew);
        Task.globalIdTask++;
    }

    public static void addEpic(Task taskNew) { //добавляем эпик
        epic.put(taskNew.idTask, new Epic(taskNew.nameTask, taskNew.descriptionTask));
        Task.globalIdTask++;
    }

    public static void addSubTask(Task taskNew, int idEpic) {//добавляем подзадачу
        if (epic.containsKey(idEpic)) {
            subTask.put(taskNew.idTask, new SubTask(taskNew.nameTask, taskNew.descriptionTask, idEpic));
            epic.get(idEpic).addSubTask(taskNew.idTask);
            Task.globalIdTask++;
        } else {
            System.out.println("Эпика с таким id не существует");
        }
    }

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

    public static void printAllTasks() { //выводит все задачи
        System.out.println("Все данные канбана:");
        printTasks();
        printEpics();
        printSubTasks();
    }

    public static void deleteAllTask() {
        task.clear();
        System.out.println("Все задачи удалены!");
    }

    public static void deleteAllSubTask() {
        subTask.clear();
        System.out.println("Все подзадачи удалены!");
    }

    public static void deleteAllEpic() {
        epic.clear();
        subTask.clear();
        System.out.println("Все эпики c их подзадачами удалены!");
    }

    public static void deleteSomething(int id) {
        String tempName;
        if (task.containsKey(id)) {
            tempName = task.get(id).nameTask;
            task.remove(id);
            System.out.println("Задача " + tempName + " [id:" + id + "] удалена!");
        } else if (epic.containsKey(id)) {
            tempName = epic.get(id).nameTask;
            epic.remove(id);
            Epic epic = Base.epic.get(id);
            for (int i = 0; i < epic.subTasks.size(); i++) {
                subTask.remove(epic.subTasks.get(i));
            }
            System.out.println("Эпик " + tempName + " [id:" + id + "] и его подзадачи удалены!");
        } else if (subTask.containsKey(id)) {
            tempName = subTask.get(id).nameTask;
            subTask.remove(id);
            System.out.println("Подзадача " + tempName + " [id:" + id + "] удалена!");
        } else {
            System.out.println("Никакого типа задач с id " + id + " не существует!");
        }
    }

    public static Task getTask(int id) {
        Task resultTask;
        if (task.containsKey(id)) {
            resultTask = task.get(id);
        } else if (epic.containsKey(id)) {
            resultTask = epic.get(id);
        } else if (subTask.containsKey(id)) {
            resultTask = subTask.get(id);
        } else {
            return null;
        }
        return resultTask;
    }

    public static void updateTask(Task taskNew) {
        if (task.containsKey(taskNew.idTask)) {
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
            if (taskNew.nameTask != null && !taskNew.nameTask.isEmpty()) {
                epic.get(taskNew.idTask).nameTask = taskNew.nameTask;
            }
            if (taskNew.descriptionTask != null && !taskNew.descriptionTask.isEmpty()) {
                epic.get(taskNew.idTask).descriptionTask = taskNew.descriptionTask;
            }
        } else if (subTask.containsKey(taskNew.idTask)) {
            if (taskNew.nameTask != null && !taskNew.nameTask.isEmpty()) {
                subTask.get(taskNew.idTask).nameTask = taskNew.nameTask;
            }
            if (taskNew.descriptionTask != null && !taskNew.descriptionTask.isEmpty()) {
                subTask.get(taskNew.idTask).descriptionTask = taskNew.descriptionTask;
            }
            if (taskNew.statusTask != null ) {
                subTask.get(taskNew.idTask).statusTask = taskNew.statusTask;
            }

            Epic epic = Base.epic.get(subTask.get(taskNew.idTask).idEpic);
            int statusResult = 0;
            for (int i = 0; i < epic.subTasks.size(); i++) {
                if(Base.subTask.get(epic.subTasks.get(i)).statusTask == StatusTask.NEW){
                    statusResult--;
                }
                if(Base.subTask.get(epic.subTasks.get(i)).statusTask == StatusTask.DONE){
                    statusResult++;
                }
            }
            if (statusResult == epic.subTasks.size()) {
                epic.statusTask = StatusTask.DONE;
            } else if (statusResult == (-epic.subTasks.size())){
                epic.statusTask = StatusTask.NEW;
            } else {
                epic.statusTask = StatusTask.IN_PROGRESS;
            }

        } else {
            System.out.println("Задача с таким id не найдена!");
        }
    }
}
