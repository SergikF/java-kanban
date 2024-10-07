package main;

import main.classes.Epic;
import main.classes.Status;
import main.classes.SubTask;
import main.classes.Task;
import main.service.Managers;
import main.service.TaskManager;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TaskManager exampleTaskManager = Managers.getDefault();

        // 1. Формируем тестовые данные
        System.out.println();
        System.out.println(" = 1 == Формируем тестовые данные ===");
        exampleTaskManager.addTask(
                new Task(exampleTaskManager.getGlobalId(), "Работа", "Просто задача",
                        Status.NEW));
        exampleTaskManager.addEpic(
                new Epic(exampleTaskManager.getGlobalId(), "Этапы", "Поэтапная работа",
                        Status.NEW, new ArrayList<Integer>()));
        exampleTaskManager.addSubTask(
                new SubTask(exampleTaskManager.getGlobalId(), "Этап 1", "Подготовка к работе",
                        Status.NEW,2));
        exampleTaskManager.addTask(
                new Task(exampleTaskManager.getGlobalId(), "Отдых", "Поехать в отпуск",
                        Status.NEW));
        exampleTaskManager.addEpic(
                new Epic(exampleTaskManager.getGlobalId(), "Обед", "Нужно утолить голод",
                        Status.NEW, new ArrayList<Integer>()));
        exampleTaskManager.addSubTask(
                new SubTask(exampleTaskManager.getGlobalId(), "Закупка", "Надо в магазин",
                        Status.NEW,5));
        exampleTaskManager.addSubTask(
                new SubTask(exampleTaskManager.getGlobalId(), "Приготовление", "Немного постоять у плиты",
                        Status.NEW,5));
        System.out.println("============================");
        System.out.println();

        // 2. Выводим списки
        System.out.println(" = 2 == Выводим списки ===");
        System.out.println(exampleTaskManager.getAllTasks()); // выводит все самостоятельные задачи
        System.out.println(exampleTaskManager.getAllEpics()); // выводит все эпики
        System.out.println(exampleTaskManager.getAllSubTasks()); // выводит все подзадачи
        System.out.println("============================");
        System.out.println();

        // 3. Получаем объект по его id
        System.out.println(" = 3 == Получаем объект по его id ===");
        System.out.println(exampleTaskManager.getTask(4)); // выводит задачу по id
        System.out.println(exampleTaskManager.getEpic(5)); // выводит эпик по id
        System.out.println(exampleTaskManager.getSubTask(3)); // выводит подзадачу по id
        System.out.println("============================");
        System.out.println();

        System.out.println(" = 4 == Обновляем задачи ===");
        // обновляем самостоятельную задачу "Работа
        System.out.println("было - " + exampleTaskManager.getTask(1));
        exampleTaskManager.updateTask(new Task(1,"Работа", "Работу работаем", Status.IN_PROGRESS));
        System.out.println("стало - " + exampleTaskManager.getTask(1));
        System.out.println();
        // обновляем эпик "Обед" для проверки изменения описания
        System.out.println("было - " + exampleTaskManager.getEpic(5));
        exampleTaskManager.updateEpic(
                new Epic(5,"Обед", "Время покушать", Status.IN_PROGRESS,
                        new ArrayList<Integer>(){{ add(6); add(7); }}));
        System.out.println("стало/было - " + exampleTaskManager.getEpic(5));
        // обновляем эпику "Обед" его подзадачу для проверки изменения статуса эпика
        exampleTaskManager.updateSubTask(
                new SubTask(6,"Закупка", "Пошли в магазин", Status.IN_PROGRESS, 5));
        System.out.println("стало - " + exampleTaskManager.getEpic(5));
        System.out.println();
        // обновляем эпику "Этапы" его подзадачу для проверки изменения статуса эпика
        System.out.println("было - " + exampleTaskManager.getEpic(2));
        exampleTaskManager.updateSubTask(
                new SubTask(3,"Этап 1", "Подготовка к работе", Status.DONE, 2));
        System.out.println("стало - " + exampleTaskManager.getEpic(2));
        System.out.println("============================");
        System.out.println();

        // 5. Выводим информацию по эпику "Обед" по его id 8
        // мне непонятно по ТЗ в каком виде должен быть этот список - есть два варианта
        System.out.println(" = 5 == Выводим список подзадач по id эпика ===");
        System.out.println("перечисление id подзадач:");
        System.out.println(exampleTaskManager.getEpic(5).getIdSubTasks());
        System.out.println("перечисление самих подзадач:");
        System.out.println(exampleTaskManager.getEpicSubTasks(5));
        System.out.println("============================");
        System.out.println();

        // Проверяем состояние истории
        System.out.println(exampleTaskManager.getHistory());
        System.out.println("============================");
        System.out.println();

        // 6. Тестируем удаление.
        System.out.println(" = 6 == Тестируем удаление по id ===");
        System.out.println("было - " + exampleTaskManager.getAllTasks());
        exampleTaskManager.deleteTask(1); // удаляем самостоятельную задачу
        System.out.println("стало - " + exampleTaskManager.getAllTasks());
        System.out.println();
        System.out.println("было - " + exampleTaskManager.getEpicSubTasks(5));
        System.out.println("было - " + exampleTaskManager.getEpic(5));
        exampleTaskManager.deleteSubTask(6); // Удаляем подзадачу эпика
        System.out.println("стало - " + exampleTaskManager.getEpicSubTasks(5));
        System.out.println("стало - " + exampleTaskManager.getEpic(5));
        System.out.println();
        System.out.println("было - " + exampleTaskManager.getAllEpics());
        exampleTaskManager.deleteEpic(2); // Удаляем эпик со всеми подзадачами
        System.out.println("стало - " + exampleTaskManager.getAllEpics());
        System.out.println("============================");
        System.out.println();

        // 7. Удаляем всё
        System.out.println(" = 7 == Удаляем всё ===");
        System.out.println("было - " + exampleTaskManager.getAllTasks()); // выводит все самостоятельные задачи
        exampleTaskManager.deleteAllTasks();
        System.out.println("стало - " + exampleTaskManager.getAllTasks());
        System.out.println();
        System.out.println("было - " + exampleTaskManager.getAllEpics()); // выводит все эпики
        System.out.println("было - " + exampleTaskManager.getAllSubTasks()); // выводит все подзадачи
        exampleTaskManager.deleteAllSubTasks();
        System.out.println("стало - " + exampleTaskManager.getAllEpics()); // выводит все эпики
        System.out.println("стало - " + exampleTaskManager.getAllSubTasks());
        System.out.println();
        System.out.println("было - " + exampleTaskManager.getAllEpics()); // выводит все эпики
        exampleTaskManager.deleteAllEpics();
        System.out.println("стало - " + exampleTaskManager.getAllEpics());
        System.out.println("============================");
        System.out.println();

        // Проверяем состояние истории
        System.out.println(exampleTaskManager.getHistory());
        System.out.println("============================");
        System.out.println();

    }
}
