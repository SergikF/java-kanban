package main;

import main.classes.Epic;
import main.classes.Status;
import main.classes.SubTask;
import main.classes.Task;
import main.service.Managers;
import main.service.TaskManagerService;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TaskManagerService exampleTaskManagerService = Managers.getDefault();

        // 1. Формируем тестовые данные
        System.out.println();
        System.out.println(" = 1 == Формируем тестовые данные ===");
        exampleTaskManagerService.addTask(
                new Task(exampleTaskManagerService.getGlobalId(), "Работа", "Просто задача",
                        Status.NEW));
        exampleTaskManagerService.addEpic(
                new Epic(exampleTaskManagerService.getGlobalId(), "Этапы", "Поэтапная работа",
                        Status.NEW, new ArrayList<Integer>()));
        exampleTaskManagerService.addSubTask(
                new SubTask(exampleTaskManagerService.getGlobalId(), "Этап 1", "Подготовка к работе",
                        Status.NEW,2));
        exampleTaskManagerService.addTask(
                new Task(exampleTaskManagerService.getGlobalId(), "Отдых", "Поехать в отпуск",
                        Status.NEW));
        exampleTaskManagerService.addEpic(
                new Epic(exampleTaskManagerService.getGlobalId(), "Обед", "Нужно утолить голод",
                        Status.NEW, new ArrayList<Integer>()));
        exampleTaskManagerService.addSubTask(
                new SubTask(exampleTaskManagerService.getGlobalId(), "Закупка", "Надо в магазин",
                        Status.NEW,5));
        exampleTaskManagerService.addSubTask(
                new SubTask(exampleTaskManagerService.getGlobalId(), "Приготовление", "Немного постоять у плиты",
                        Status.NEW,5));
        System.out.println("============================");
        System.out.println();

        // 2. Выводим списки
        System.out.println(" = 2 == Выводим списки ===");
        System.out.println(exampleTaskManagerService.getAllTasks()); // выводит все самостоятельные задачи
        System.out.println(exampleTaskManagerService.getAllEpics()); // выводит все эпики
        System.out.println(exampleTaskManagerService.getAllSubTasks()); // выводит все подзадачи
        System.out.println("============================");
        System.out.println();

        // 3. Получаем объект по его id
        System.out.println(" = 3 == Получаем объект по его id ===");
        System.out.println(exampleTaskManagerService.getTask(4)); // выводит задачу по id
        System.out.println(exampleTaskManagerService.getEpic(5)); // выводит эпик по id
        System.out.println(exampleTaskManagerService.getSubTask(3)); // выводит подзадачу по id
        System.out.println("============================");
        System.out.println();

        System.out.println(" = 4 == Обновляем задачи ===");
        // обновляем самостоятельную задачу "Работа
        System.out.println("было - " + exampleTaskManagerService.getTask(1));
        exampleTaskManagerService.updateTask(new Task(1,"Работа", "Работу работаем", Status.IN_PROGRESS));
        System.out.println("стало - " + exampleTaskManagerService.getTask(1));
        System.out.println();
        // обновляем эпик "Обед" для проверки изменения описания
        System.out.println("было - " + exampleTaskManagerService.getEpic(5));
        exampleTaskManagerService.updateEpic(
                new Epic(5,"Обед", "Время покушать", Status.IN_PROGRESS,
                        new ArrayList<Integer>(){{ add(6); add(7); }}));
        System.out.println("стало/было - " + exampleTaskManagerService.getEpic(5));
        // обновляем эпику "Обед" его подзадачу для проверки изменения статуса эпика
        exampleTaskManagerService.updateSubTask(
                new SubTask(6,"Закупка", "Пошли в магазин", Status.IN_PROGRESS, 5));
        System.out.println("стало - " + exampleTaskManagerService.getEpic(5));
        System.out.println();
        // обновляем эпику "Этапы" его подзадачу для проверки изменения статуса эпика
        System.out.println("было - " + exampleTaskManagerService.getEpic(2));
        exampleTaskManagerService.updateSubTask(
                new SubTask(3,"Этап 1", "Подготовка к работе", Status.DONE, 2));
        System.out.println("стало - " + exampleTaskManagerService.getEpic(2));
        System.out.println("============================");
        System.out.println();

        // 5. Выводим информацию по эпику "Обед" по его id 8
        // мне непонятно по ТЗ в каком виде должен быть этот список - есть два варианта
        System.out.println(" = 5 == Выводим список подзадач по id эпика ===");
        System.out.println("перечисление id подзадач:");
        System.out.println(exampleTaskManagerService.getEpic(5).getIdSubTasks());
        System.out.println("перечисление самих подзадач:");
        System.out.println(exampleTaskManagerService.getEpicSubTasks(5));
        System.out.println("============================");
        System.out.println();

        // Проверяем состояние истории
        System.out.println(exampleTaskManagerService.getHistory());
        System.out.println("============================");
        System.out.println();

        // 6. Тестируем удаление.
        System.out.println(" = 6 == Тестируем удаление по id ===");
        System.out.println("было - " + exampleTaskManagerService.getAllTasks());
        exampleTaskManagerService.deleteTask(1); // удаляем самостоятельную задачу
        System.out.println("стало - " + exampleTaskManagerService.getAllTasks());
        System.out.println();
        System.out.println("было - " + exampleTaskManagerService.getEpicSubTasks(5));
        System.out.println("было - " + exampleTaskManagerService.getEpic(5));
        exampleTaskManagerService.deleteSubTask(6); // Удаляем подзадачу эпика
        System.out.println("стало - " + exampleTaskManagerService.getEpicSubTasks(5));
        System.out.println("стало - " + exampleTaskManagerService.getEpic(5));
        System.out.println();
        System.out.println("было - " + exampleTaskManagerService.getAllEpics());
        exampleTaskManagerService.deleteEpic(2); // Удаляем эпик со всеми подзадачами
        System.out.println("стало - " + exampleTaskManagerService.getAllEpics());
        System.out.println("============================");
        System.out.println();

        // 7. Удаляем всё
        System.out.println(" = 7 == Удаляем всё ===");
        System.out.println("было - " + exampleTaskManagerService.getAllTasks()); // выводит все самостоятельные задачи
        exampleTaskManagerService.deleteAllTasks();
        System.out.println("стало - " + exampleTaskManagerService.getAllTasks());
        System.out.println();
        System.out.println("было - " + exampleTaskManagerService.getAllEpics()); // выводит все эпики
        System.out.println("было - " + exampleTaskManagerService.getAllSubTasks()); // выводит все подзадачи
        exampleTaskManagerService.deleteAllSubTasks();
        System.out.println("стало - " + exampleTaskManagerService.getAllEpics()); // выводит все эпики
        System.out.println("стало - " + exampleTaskManagerService.getAllSubTasks());
        System.out.println();
        System.out.println("было - " + exampleTaskManagerService.getAllEpics()); // выводит все эпики
        exampleTaskManagerService.deleteAllEpics();
        System.out.println("стало - " + exampleTaskManagerService.getAllEpics());
        System.out.println("============================");
        System.out.println();

        // Проверяем состояние истории
        System.out.println(exampleTaskManagerService.getHistory());
        System.out.println("============================");
        System.out.println();

    }
}
