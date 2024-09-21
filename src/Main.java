public class Main {

    public static void main(String[] args) {

        // формируем тестовый канбан
        Manager.addTask(new Task("Работа", "Просто задача"));
        Manager.addEpic(new Task("Этапы", "Поэтапная работа"));
        Manager.addSubTask(new Task("Этап 1", "Подготовка к работе"),2);
        Manager.addTask(new Task("Отдых", "Поехать в отпуск"));
        Manager.addSubTask(new Task("Этап 2", "Предварительное проектирование"),2);
        Manager.addTask(new Task("Прогулка", "Отдых полезен"));
        Manager.addTask(new Task("Тренировка", "Спорт это жизнь"));
        Manager.addEpic(new Task("Обед", "Нужно утолить голод"));
        Manager.addSubTask(new Task("Закупка", "Пошли в магазин"),8);
        Manager.addSubTask(new Task("Приготовление", "Немного постоять у плиты"),8);
        Manager.addSubTask(new Task("Покушать", "Пора покушать"),8);

        Manager.printAllTasks(); // выводит все задачи
        // Manager.deleteAllTask(); // удаляет все задачи
        // Manager.printAllTasks(); // выводит все задачи

        System.out.println("============================");
        System.out.println();
        //System.out.println(Manager.getTask(6));
        //System.out.println(Manager.getTask(8));
        //System.out.println(Manager.getTask(5));
        Manager.updateTask(new Task(11,"", "", StatusTask.IN_PROGRESS));
        Manager.updateTask(new Task(4,null, null, StatusTask.IN_PROGRESS));
        Manager.updateTask(new Task(3,"", "", StatusTask.DONE));
        Manager.updateTask(new Task(5,null, null, StatusTask.DONE));

        System.out.println("============================");
        Manager.printAllTasks();

        //System.out.println(Base.task);
        //System.out.println(Base.epic);
        //System.out.println(Base.subTask);

        System.out.println("============================");
        Manager.printEpic(8);
        Manager.printEpic(2);


    }
}
