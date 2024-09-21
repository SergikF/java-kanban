public class Main {

    public static void main(String[] args) {

        // 1. Формируем тестовый канбан
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

        // 2. Выводим списки
        System.out.println(" = 2 == Выводим списки ===");
        Manager.printTasks(); // выводит все самостоятельные задачи
        Manager.printEpics(); //// выводит все эпики
        Manager.printSubTasks(); // выводит все подзадачи
        System.out.println("============================");
        System.out.println();

        // 3. Получаем объект по его id
        System.out.println(" = 3 == Получаем объект по его id ===");
        System.out.println(Manager.getTask(6));
        System.out.println(Manager.getTask(8));
        System.out.println(Manager.getTask(5));
        System.out.println("============================");
        System.out.println();

        // 4. Обновляем задачи ( сделано так, что если нужно изменять наименование или описание - передаются данные
        // если же текстовую часть менять не нужно, а только статус - не передаём текс или передаём пустые строки.
        // обновляем подзадачу, и проверяем изменение статуса эпика "Обед"
        System.out.println(" = 4 == Обновляем задачи ===");
        Manager.updateTask(new Task(11,"", "", StatusTask.IN_PROGRESS));
        // обновляем самостоятельную задачу "Отдых"
        Manager.updateTask(new Task(4,null, null, StatusTask.IN_PROGRESS));
        // обновляем эпику "Этапы" его подзадачи для проверки изменения статуса эпика
        Manager.updateTask(new Task(3,"", "", StatusTask.DONE));
        Manager.updateTask(new Task(5,null, null, StatusTask.DONE));
        Manager.printAllTasks(); // выводим канбан с новыми данными для проверки
        System.out.println("============================");
        System.out.println();

        // 5. Выводим информацию по эпику "Обед" по его id
        System.out.println(" = 5 == Тестируем удаление по id ===");
        Manager.printEpic(8);
        System.out.println("============================");
        System.out.println();

        // 6. Тестируем удаление.
        System.out.println(" = 6 == Тестируем удаление по id ===");
        // Так как удаление идёт по id - то не разделяем, что удалять - по id определяем что это и удаляем
        Manager.deleteSomething(6); // удаляем самостоятельную задачу
        Manager.deleteSomething(10); // Удаляем подзадачу эпика
        Manager.deleteSomething(2); // Удаляем эпик со всеми подзадачами
        Manager.printAllTasks(); // выводим канбан с новыми данными для проверки
        System.out.println("============================");
        System.out.println();

        // 7. Удаляем всё
        System.out.println(" = 7 == Удаляем всё ===");
        Manager.deleteAllTask();
        Manager.deleteAllSubTask();
        Manager.deleteAllEpic();
        Manager.printAllTasks();
        System.out.println("============================");
    }
}
