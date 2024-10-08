package test;

import main.classes.Epic;
import main.classes.Status;
import main.classes.SubTask;
import main.classes.Task;
import main.service.Managers;
import main.service.TaskManagerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

class TestCase {

    private static TaskManagerService testManager;
    private static Task taskItem;
    private static Epic epicItem;
    private static SubTask subTaskItem;

    @BeforeEach
    void initializeTaskManager() {
    // Проверяем создание менеджера через утилитарный класс, используем его в тестах
        testManager = Managers.getDefault();
        Assertions.assertNotNull(testManager); // проверяем что менеджер создан
        Assertions.assertNotNull(testManager.getAllTasks()); // проверяем что список задач создан
        Assertions.assertNotNull(testManager.getAllEpics()); // проверяем что список эпиков создан
        Assertions.assertNotNull(testManager.getAllSubTasks()); // проверяем что список субзадач создан
        Assertions.assertNotNull(testManager.getHistory()); // проверяем что история создана

        // Создаём элементы для добавления в менеджер
        taskItem = new Task(testManager.getGlobalId(),
                "Работа", "Просто задача", Status.NEW);
        epicItem = new Epic(testManager.getGlobalId(),
                "Этапы", "Поэтапная работа", Status.NEW, new ArrayList<Integer>());
        subTaskItem = new SubTask(testManager.getGlobalId(),
                "Этап 1", "Подготовка к работе", Status.NEW,2);

        // Добавляем элементы в менеджер
        testManager.addTask(taskItem);
        testManager.addEpic(epicItem);
        testManager.addSubTask(subTaskItem);

    }

    @Test
    void addItem() {

        // Проверяем, что globalId имеет последний свободный id номер
        Assertions.assertEquals(4, testManager.getGlobalId());
        // Проверяем, что элементы добавлены в менеджер
        // затем ищем элемент в менеджере по его id
        // и сравниваем с элементом созданным в тесте
        Assertions.assertEquals(taskItem, testManager.getTask(taskItem.getId()));
        Assertions.assertEquals(epicItem, testManager.getEpic(epicItem.getId()));
        Assertions.assertEquals(subTaskItem, testManager.getSubTask(subTaskItem.getId()));

        // Проверяем идентичность отправляемых элементов в менеджер с сохранёнными в менеджере по всем значениям
        // Проверяем обычные задачи
        Assertions.assertEquals(taskItem.getName(), testManager.getTask(taskItem.getId()).getName());
        Assertions.assertEquals(taskItem.getDescription(), testManager.getTask(taskItem.getId()).getDescription());
        Assertions.assertEquals(taskItem.getStatus(), testManager.getTask(taskItem.getId()).getStatus());
        // Проверяем эпики
        Assertions.assertEquals(epicItem.getName(), testManager.getEpic(epicItem.getId()).getName());
        Assertions.assertEquals(epicItem.getDescription(), testManager.getEpic(epicItem.getId()).getDescription());
        Assertions.assertEquals(epicItem.getStatus(), testManager.getEpic(epicItem.getId()).getStatus());
        Assertions.assertEquals(epicItem.getIdSubTasks(), testManager.getEpic(epicItem.getId()).getIdSubTasks());
        // Проверяем подзадачи
        Assertions.assertEquals(subTaskItem.getName(), testManager.getSubTask(subTaskItem.getId()).getName());
        Assertions.assertEquals(subTaskItem.getDescription(),
                testManager.getSubTask(subTaskItem.getId()).getDescription());
        Assertions.assertEquals(subTaskItem.getStatus(), testManager.getSubTask(subTaskItem.getId()).getStatus());
        Assertions.assertEquals(subTaskItem.getIdEpic(), testManager.getSubTask(subTaskItem.getId()).getIdEpic());

    }


    @Test
    void testItems() {
        // проверяем равенство элементов с одним и тем-же id
        Task taskItem1 = testManager.getTask(1);
        Task taskItem2 = testManager.getTask(1);
        Assertions.assertEquals(taskItem1, taskItem2);
        Epic epicItem1 = testManager.getEpic(2);
        Epic epicItem2 = testManager.getEpic(2);
        Assertions.assertEquals(epicItem1, epicItem2);
        SubTask subTaskItem1 = testManager.getSubTask(3);
        SubTask subTaskItem2 = testManager.getSubTask(3);
        Assertions.assertEquals(subTaskItem1, subTaskItem2);

    }

    @Test
    void testHistory() {
        // Проверяем, что история пустая
        Assertions.assertEquals(0, testManager.getHistory().size(), "История не пустая.");
        // Добавляем элемент в историю при помощи запроса к менеджеру
        Task taskItem1 = testManager.getTask(1);
        // проверяем, что история изменилась на 1
        Assertions.assertEquals(1, testManager.getHistory().size(), "В историю не попала запись.");
        // извлекаем из истории элемент
        Task taskItem2 = testManager.getHistory().get(0);
        // Проверяем идентичность задачи менеджера и элемента истории
        Assertions.assertEquals(taskItem1, taskItem2, "записи не соответствуют.");
        // Изменяем содержимое задачи в менеджере, при этом проверяем, что нет конфликта при взаимодействии
        // двух задач с одним id - происходит замена данных.
        taskItem = new Task(1,
                "Не работа", "Задача в работе", Status.IN_PROGRESS);
        testManager.updateTask(taskItem);
        // делаем запрос к задаче, для фиксации в истории этого запроса.
        taskItem2 = testManager.getTask(1);
        // Сравниваем содержимое первой и второй записи истории с содержимым задачи 1 до и после изменения.
        // Первая запись будет соответствовать taskItem1, а вторая - taskItem2.
        Assertions.assertEquals(taskItem1, testManager.getHistory().get(0), "записи не соответствуют.");
        Assertions.assertEquals(taskItem2, testManager.getHistory().get(1), "записи не соответствуют.");

    }
}