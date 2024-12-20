package main;

import main.classes.Epic;
import main.classes.Status;
import main.classes.SubTask;
import main.classes.Task;
import main.service.ManagerSaveException;
import main.service.Managers;
import main.service.TaskManager;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {

        // Проверяем работу менеджера задач
        File testFile;
        try {
            testFile = File.createTempFile("test", ".csv");
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось создать временный файл");
        }
        TaskManager exampleTM1 = Managers.getFile(testFile);

        // 1. Формируем тестовые данные
        System.out.println();
        System.out.println(" = 1 == Формируем тестовые данные ===");
        exampleTM1.addTask(new Task(0,
                "Работа", "Просто задача", Status.NEW,
                LocalDateTime.of(2024, 1, 1, 10, 5, 0),
                Duration.ofMinutes(50)));
        exampleTM1.addEpic(new Epic(0,
                "Этапы", "Поэтапная работа", Status.NEW));
        exampleTM1.addSubTask(new SubTask(0,
                "Этап 1", "Подготовка к работе", Status.NEW, 2,
                LocalDateTime.of(2024, 1, 1, 8, 0, 0),
                Duration.ofHours(23)));
        exampleTM1.addTask(new Task(0,
                "Отдых", "Поехать в отпуск", Status.NEW));
        exampleTM1.addEpic(new Epic(0,
                "Обед", "Нужно утолить голод", Status.NEW));
        exampleTM1.addSubTask(new SubTask(0,
                "Закупка", "Надо в магазин", Status.NEW, 5,
                LocalDateTime.of(2024, 1, 8, 9, 0, 0),
                Duration.ofHours(6)));
        exampleTM1.addSubTask(new SubTask(0,
                "Приготовление", "Немного постоять у плиты", Status.NEW, 5,
                LocalDateTime.of(2024, 1, 6, 11, 0, 0),
                Duration.ofHours(1)));
        System.out.println("============================");
        System.out.println();

        // 2. Выводим списки
        System.out.println(" = 2 == Выводим списки ===");
        System.out.println(exampleTM1.getAllTasks()); // выводит все самостоятельные задачи
        System.out.println(exampleTM1.getAllEpics()); // выводит все эпики
        System.out.println(exampleTM1.getAllSubTasks()); // выводит все подзадачи
        System.out.println("============================");
        System.out.println();
        System.out.println("Выводим историю");
        System.out.println(exampleTM1.getHistory());
        System.out.println("============================");
        System.out.println();

        // считываем файл в новый экземпляр менеджера задач и проводим проверки с ним
        TaskManager exampleTM2 = Managers.getFile(testFile);

        System.out.println("============================");
        System.out.println("Новый менеджер задач");
        System.out.println("============================");
        System.out.println();

        // 2. Выводим списки
        System.out.println(" = 2 == Выводим списки ===");
        System.out.println(exampleTM2.getAllTasks()); // выводит все самостоятельные задачи
        System.out.println(exampleTM2.getAllEpics()); // выводит все эпики
        System.out.println(exampleTM2.getAllSubTasks()); // выводит все подзадачи
        System.out.println("============================");
        System.out.println();
        System.out.println("Выводим историю");
        System.out.println(exampleTM2.getHistory());
        System.out.println("============================");
        System.out.println();

        // 3. Получаем объект по его id
        System.out.println(" = 3 == Получаем объект по его id ===");
        System.out.println(exampleTM2.getTask(4)); // выводит задачу по id
        System.out.println();
        System.out.println("Выводим историю");
        System.out.println(exampleTM2.getHistory());
        System.out.println();
        System.out.println(exampleTM2.getTask(4)); // выводит задачу по id
        System.out.println(exampleTM2.getEpic(5)); // выводит эпик по id
        System.out.println();
        System.out.println("Выводим историю");
        System.out.println(exampleTM2.getHistory());
        System.out.println();
        System.out.println(exampleTM2.getEpic(5)); // выводит эпик по id
        System.out.println(exampleTM2.getSubTask(3)); // выводит подзадачу по id
        System.out.println(exampleTM2.getTask(4)); // выводит задачу по id
        System.out.println();
        System.out.println("Выводим историю");
        System.out.println(exampleTM2.getHistory());
        System.out.println();

        System.out.println(" = 4 == Обновляем задачи ===");
        // обновляем самостоятельную задачу "Работа
        System.out.println("было - " + exampleTM2.getTask(1));
        exampleTM2.updateTask(new Task(1, "Работа",
                "Работу работаем", Status.IN_PROGRESS));
        System.out.println("стало - " + exampleTM2.getTask(1));
        System.out.println();
        // обновляем эпик "Обед" для проверки изменения описания
        System.out.println("было - " + exampleTM2.getEpic(5));
        exampleTM2.updateEpic(new Epic(5, "Обед",
                "Время покушать", Status.IN_PROGRESS));
        System.out.println("стало/было - " + exampleTM2.getEpic(5));
        // обновляем эпику "Обед" его подзадачу для проверки изменения статуса эпика
        exampleTM2.updateSubTask(new SubTask(6, "Закупка",
                "Пошли в магазин", Status.IN_PROGRESS, 5));
        System.out.println("стало - " + exampleTM2.getEpic(5));
        System.out.println();
        // обновляем эпику "Этапы" его подзадачу для проверки изменения статуса эпика
        System.out.println("было - " + exampleTM2.getEpic(2));
        exampleTM2.updateSubTask(new SubTask(3, "Этап 1",
                "Подготовка к работе", Status.DONE, 2));
        System.out.println("стало - " + exampleTM2.getEpic(2));
        exampleTM2.addTask(new Task(0,
                "Попытка 5", "Новая задача после загрузки", Status.NEW,
                LocalDateTime.of(2024, 1, 6, 11, 40, 0),
                Duration.ofHours(1)));
        exampleTM2.addSubTask(new SubTask(0,
                "Попытка 6", "Новая подзадача после загрузки", Status.NEW, 2,
                LocalDateTime.of(2024, 1, 1, 10, 50, 0),
                Duration.ofHours(1)));
        System.out.println("============================");
        System.out.println();

        // 5. Выводим информацию по эпику "Обед" по его id 8
        // мне непонятно по ТЗ в каком виде должен быть этот список - есть два варианта
        System.out.println(" = 5 == Выводим список подзадач по id эпика ===");
        System.out.println(exampleTM2.getEpicSubTasks(5));
        System.out.println("============================");
        System.out.println();
        System.out.println("Выводим историю");
        System.out.println(exampleTM2.getHistory());
        System.out.println("============================");
        System.out.println();

        // 6. Тестируем удаление.
        System.out.println(" = 6 == Тестируем удаление по id ===");
        System.out.println("было - " + exampleTM2.getAllTasks());
        exampleTM2.deleteTask(1); // удаляем самостоятельную задачу
        System.out.println("стало - " + exampleTM2.getAllTasks());
        System.out.println();
        System.out.println("Выводим историю");
        System.out.println(exampleTM2.getHistory());
        System.out.println();
        System.out.println("было - " + exampleTM2.getEpicSubTasks(5));
        System.out.println("было - " + exampleTM2.getEpic(5));
        exampleTM2.deleteSubTask(6); // Удаляем подзадачу эпика
        System.out.println("стало - " + exampleTM2.getEpicSubTasks(5));
        System.out.println("стало - " + exampleTM2.getEpic(5));
        System.out.println();
        System.out.println("Выводим историю");
        System.out.println(exampleTM2.getHistory());
        System.out.println();
        System.out.println("было - " + exampleTM2.getAllEpics());
        exampleTM2.deleteEpic(2); // Удаляем эпик со всеми подзадачами
        System.out.println("стало - " + exampleTM2.getAllEpics());
        System.out.println();
        System.out.println("Выводим историю");
        System.out.println(exampleTM2.getHistory());
        System.out.println("============================");

        // 7. Удаляем всё
        System.out.println(" = 7 == Удаляем всё ===");
        System.out.println("было - " + exampleTM2.getAllTasks()); // выводит все самостоятельные задачи
        exampleTM2.deleteAllTasks();
        System.out.println("стало - " + exampleTM2.getAllTasks());
        System.out.println();
        System.out.println("Выводим историю");
        System.out.println(exampleTM2.getHistory());
        System.out.println();
        System.out.println("было - " + exampleTM2.getAllEpics()); // выводит все эпики
        System.out.println("было - " + exampleTM2.getAllSubTasks()); // выводит все подзадачи
        exampleTM2.deleteAllSubTasks();
        System.out.println("стало - " + exampleTM2.getAllEpics()); // выводит все эпики
        System.out.println("стало - " + exampleTM2.getAllSubTasks());
        System.out.println();
        System.out.println("Выводим историю");
        System.out.println(exampleTM2.getHistory());
        System.out.println();
        System.out.println("было - " + exampleTM2.getAllEpics()); // выводит все эпики
        exampleTM2.deleteAllEpics();
        System.out.println("стало - " + exampleTM2.getAllEpics());
        System.out.println();
        System.out.println("Выводим историю");
        System.out.println(exampleTM2.getHistory());
        System.out.println("============================");
        System.out.println();

    }
}
