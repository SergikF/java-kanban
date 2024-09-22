import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        // 1. Формируем тестовые данные
        System.out.println();
        System.out.println(" = 1 == Формируем тестовые данные ===");
        taskManager.addTask(
                new Task(taskManager.getGlobalId(), "Работа", "Просто задача",
                        Status.NEW));
        taskManager.addEpic(
                new Epic(taskManager.getGlobalId(), "Этапы", "Поэтапная работа",
                        Status.NEW, new ArrayList<Integer>()));
        taskManager.addSubTask(
                new SubTask(taskManager.getGlobalId(), "Этап 1", "Подготовка к работе",
                        Status.NEW,2));
        taskManager.addTask(
                new Task(taskManager.getGlobalId(), "Отдых", "Поехать в отпуск",
                        Status.NEW));
        taskManager.addEpic(
                new Epic(taskManager.getGlobalId(), "Обед", "Нужно утолить голод",
                        Status.NEW, new ArrayList<Integer>()));
        taskManager.addSubTask(
                new SubTask(taskManager.getGlobalId(), "Закупка", "Надо в магазин",
                        Status.NEW,5));
        taskManager.addSubTask(
                new SubTask(taskManager.getGlobalId(), "Приготовление", "Немного постоять у плиты",
                        Status.NEW,5));
        System.out.println("============================");
        System.out.println();

        // 2. Выводим списки
        System.out.println(" = 2 == Выводим списки ===");
        System.out.println(taskManager.getAllTasks()); // выводит все самостоятельные задачи
        System.out.println(taskManager.getAllEpics()); // выводит все эпики
        System.out.println(taskManager.getAllSubTasks()); // выводит все подзадачи
        System.out.println("============================");
        System.out.println();

        // 3. Получаем объект по его id
        System.out.println(" = 3 == Получаем объект по его id ===");
        System.out.println(taskManager.getTask(4)); // выводит задачу по id
        System.out.println(taskManager.getEpic(5)); // выводит эпик по id
        System.out.println(taskManager.getSubTask(3)); // выводит подзадачу по id
        System.out.println("============================");
        System.out.println();

        System.out.println(" = 4 == Обновляем задачи ===");
        // обновляем самостоятельную задачу "Работа
        System.out.println("было - " + taskManager.getTask(1));
        taskManager.updateTask(new Task(1,"Работа", "Работу работаем", Status.IN_PROGRESS));
        System.out.println("стало - " + taskManager.getTask(1));
        System.out.println();
        // обновляем эпик "Обед" для проверки изменения описания
        System.out.println("было - " + taskManager.getEpic(5));
        taskManager.updateEpic(
                new Epic(5,"Обед", "Время покушать", Status.IN_PROGRESS,
                        new ArrayList<Integer>(){{ add(6); add(7); }}));
        System.out.println("стало/было - " + taskManager.getEpic(5));
        // обновляем эпику "Обед" его подзадачу для проверки изменения статуса эпика
        taskManager.updateSubTask(
                new SubTask(6,"Закупка", "Пошли в магазин", Status.IN_PROGRESS, 5));
        System.out.println("стало - " + taskManager.getEpic(5));
        System.out.println();
        // обновляем эпику "Этапы" его подзадачу для проверки изменения статуса эпика
        System.out.println("было - " + taskManager.getEpic(2));
        taskManager.updateSubTask(
                new SubTask(3,"Этап 1", "Подготовка к работе", Status.DONE, 2));
        System.out.println("стало - " + taskManager.getEpic(2));
        System.out.println("============================");
        System.out.println();

        // 5. Выводим информацию по эпику "Обед" по его id 8
        // мне непонятно по ТЗ в каком виде должен быть этот список - есть два варианта
        System.out.println(" = 5 == Выводим список подзадач по id эпика ===");
        System.out.println("перечисление id подзадач:");
        System.out.println(taskManager.getEpic(5).getIdSubTasks());
        System.out.println("перечисление самих подзадач:");
        System.out.println(taskManager.getEpicSubTasks(5));
        System.out.println("============================");
        System.out.println();

        // 6. Тестируем удаление.
        System.out.println(" = 6 == Тестируем удаление по id ===");
        System.out.println("было - " + taskManager.getAllTasks());
        taskManager.deleteTask(1); // удаляем самостоятельную задачу
        System.out.println("стало - " + taskManager.getAllTasks());
        System.out.println();
        System.out.println("было - " + taskManager.getEpicSubTasks(5));
        System.out.println("было - " + taskManager.getEpic(5));
        taskManager.deleteSubTask(6); // Удаляем подзадачу эпика
        System.out.println("стало - " + taskManager.getEpicSubTasks(5));
        System.out.println("стало - " + taskManager.getEpic(5));
        System.out.println();
        System.out.println("было - " + taskManager.getAllEpics());
        taskManager.deleteEpic(2); // Удаляем эпик со всеми подзадачами
        System.out.println("стало - " + taskManager.getAllEpics());
        System.out.println("============================");
        System.out.println();

        // 7. Удаляем всё
        System.out.println(" = 7 == Удаляем всё ===");
        System.out.println("было - " + taskManager.getAllTasks()); // выводит все самостоятельные задачи
        taskManager.deleteAllTasks();
        System.out.println("стало - " + taskManager.getAllTasks());
        System.out.println();
        System.out.println("было - " + taskManager.getAllEpics()); // выводит все эпики
        System.out.println("было - " + taskManager.getAllSubTasks()); // выводит все подзадачи
        taskManager.deleteAllSubTasks();
        System.out.println("стало - " + taskManager.getAllEpics()); // выводит все эпики
        System.out.println("стало - " + taskManager.getAllSubTasks());
        System.out.println();
        System.out.println("было - " + taskManager.getAllEpics()); // выводит все эпики
        taskManager.deleteAllEpics();
        System.out.println("стало - " + taskManager.getAllEpics());
        System.out.println("============================");

    }
}
