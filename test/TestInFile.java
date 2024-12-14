package test;

import main.classes.Epic;
import main.classes.Status;
import main.classes.SubTask;
import main.classes.Task;
import main.service.ManagerSaveException;
import main.service.Managers;
import main.service.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

class TestInFile {

    private static TaskManager testManager;
    private static Task taskItem;
    private static Epic epicItem;
    private static SubTask subTaskItem;

    @BeforeEach
    void initializeTaskManager() {
        // Создаем пустой файл для менеджера задач
        File testFile;
        try {
            testFile = File.createTempFile("test", ".csv");
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось создать временный файл");
        }

        // Проверяем что файл создан и он пустой
        Assertions.assertNotNull(testFile);
        // Проверяем что файл пустой
        Assertions.assertEquals(0, testFile.length());

        // Проверяем создание менеджера через утилитарный класс, используем его в тестах
        testManager = Managers.getFile(testFile);

        Assertions.assertNotNull(testManager); // проверяем что менеджер создан
        Assertions.assertNotNull(testManager.getAllTasks()); // проверяем что список задач создан
        Assertions.assertNotNull(testManager.getAllEpics()); // проверяем что список эпиков создан
        Assertions.assertNotNull(testManager.getAllSubTasks()); // проверяем что список подзадач создан
        Assertions.assertNotNull(testManager.getHistory()); // проверяем что история создана

        // Если при создании менеджера не было ошибок - значит пустой файл был считан и тест пройден.

    }

    @Test
    void testFilesOperations() {
        // Проверяем что менеджер сохраняет данные в файл

        // Создаём элементы для добавления в менеджер
        taskItem = new Task(0,
                "Работа", "Просто задача", Status.NEW,
                LocalDateTime.of(2024, 1, 1, 10, 5, 0),
                Duration.ofMinutes(50));
        epicItem = new Epic(0,
                "Этапы", "Поэтапная работа", Status.NEW);
        subTaskItem = new SubTask(0,
                "Этап 1", "Подготовка к работе", Status.NEW, 2,
                LocalDateTime.of(2024, 1, 5, 8, 5, 0),
                Duration.ofMinutes(250));

        // Добавляем элементы в менеджер
        testManager.addTask(taskItem);
        testManager.addEpic(epicItem);
        testManager.addSubTask(subTaskItem);

        // Проверяем что файл не пустой
        Assertions.assertNotEquals(0, testManager.getFile().length());

        // Считываем данные из файла в другой экземпляр менеджера
        TaskManager testManager2 = Managers.getFile(testManager.getFile());

        // Проверяем что данные из файла были считаны правильно и равны исходным
        Assertions.assertEquals(testManager.getAllTasks(), testManager2.getAllTasks());
        Assertions.assertEquals(testManager.getAllEpics(), testManager2.getAllEpics());
        Assertions.assertEquals(testManager.getAllSubTasks(), testManager2.getAllSubTasks());
        // Проверяем идентичность полученные элементы из файла с исходными по всем значениям
        Assertions.assertEquals(taskItem, testManager2.getTask(taskItem.getId()));
        Assertions.assertEquals(epicItem, testManager2.getEpic(epicItem.getId()));
        Assertions.assertEquals(subTaskItem, testManager2.getSubTask(subTaskItem.getId()));

        // Пройденные тесты говорят о правильности чтения из файла.

        // Удаляем содержимое второго экземпляра менеджера
        testManager2.deleteAllSubTasks();
        testManager2.deleteAllEpics();
        testManager2.deleteAllTasks();
        // Проверяем что менеджер пустой
        Assertions.assertTrue(testManager2.getAllTasks().isEmpty());
        Assertions.assertTrue(testManager2.getAllEpics().isEmpty());
        Assertions.assertTrue(testManager2.getAllSubTasks().isEmpty());
        // Проверяем, что файл второго менеджера пустой
        Assertions.assertEquals(0, testManager2.getFile().length());

    }

    @Test
    void addItem() {

        // Создаём элементы для добавления в менеджер
        taskItem = new Task(0,
                "Работа", "Просто задача", Status.NEW,
                LocalDateTime.of(2024, 1, 5, 10, 5, 0),
                Duration.ofMinutes(50));
        epicItem = new Epic(0,
                "Этапы", "Поэтапная работа", Status.NEW);
        subTaskItem = new SubTask(0,
                "Этап 1", "Подготовка к работе", Status.NEW, 2,
                LocalDateTime.of(2024, 1, 1, 8, 5, 0),
                Duration.ofMinutes(250));

        // Добавляем элементы в менеджер
        testManager.addTask(taskItem);
        testManager.addEpic(epicItem);
        testManager.addSubTask(subTaskItem);

        // Проверяем, что globalId имеет последний свободный id номер
        Assertions.assertEquals(4, testManager.getGlobalId());
        // Проверяем, что элементы добавлены в менеджере
        // затем ищем элемент в менеджере по его id
        // и сравниваем с элементом созданным в тесте
        Assertions.assertEquals(taskItem, testManager.getTask(taskItem.getId()));
        Assertions.assertEquals(epicItem, testManager.getEpic(epicItem.getId()));
        Assertions.assertEquals(subTaskItem, testManager.getSubTask(subTaskItem.getId()));

        // Проверяем идентичность отправляемых элементов в менеджере с сохранёнными в менеджере по всем значениям
        // Проверяем обычные задачи
        Assertions.assertEquals(taskItem.getName(),
                testManager.getTask(taskItem.getId()).getName());
        Assertions.assertEquals(taskItem.getDescription(),
                testManager.getTask(taskItem.getId()).getDescription());
        Assertions.assertEquals(taskItem.getStatus(),
                testManager.getTask(taskItem.getId()).getStatus());
        // Проверяем эпики
        Assertions.assertEquals(epicItem.getName(),
                testManager.getEpic(epicItem.getId()).getName());
        Assertions.assertEquals(epicItem.getDescription(),
                testManager.getEpic(epicItem.getId()).getDescription());
        Assertions.assertEquals(epicItem.getStatus(),
                testManager.getEpic(epicItem.getId()).getStatus());
        // Проверяем подзадачи
        Assertions.assertEquals(subTaskItem.getName(),
                testManager.getSubTask(subTaskItem.getId()).getName());
        Assertions.assertEquals(subTaskItem.getDescription(),
                testManager.getSubTask(subTaskItem.getId()).getDescription());
        Assertions.assertEquals(subTaskItem.getStatus(),
                testManager.getSubTask(subTaskItem.getId()).getStatus());
        Assertions.assertEquals(subTaskItem.getIdEpic(),
                testManager.getSubTask(subTaskItem.getId()).getIdEpic());

    }


    @Test
    void testItems() {

        // Создаём элементы для добавления в менеджер
        taskItem = new Task(0,
                "Работа", "Просто задача", Status.NEW,
                LocalDateTime.of(2024, 1, 1, 10, 5, 0),
                Duration.ofMinutes(50));
        epicItem = new Epic(0,
                "Этапы", "Поэтапная работа", Status.NEW);
        subTaskItem = new SubTask(0,
                "Этап 1", "Подготовка к работе", Status.NEW, 2,
                LocalDateTime.of(2024, 1, 1, 10, 5, 0),
                Duration.ofMinutes(250));

        // Добавляем элементы в менеджер
        testManager.addTask(taskItem);
        testManager.addEpic(epicItem);
        testManager.addSubTask(subTaskItem);

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
    void testHistory1() {

        // Создаём элементы для добавления в менеджер
        taskItem = new Task(0,
                "Работа", "Просто задача", Status.NEW,
                LocalDateTime.of(2024, 1, 1, 12, 5, 0),
                Duration.ofMinutes(50));
        epicItem = new Epic(0,
                "Этапы", "Поэтапная работа", Status.NEW);
        subTaskItem = new SubTask(0,
                "Этап 1", "Подготовка к работе", Status.NEW, 2,
                LocalDateTime.of(2024, 1, 1, 8, 5, 0),
                Duration.ofMinutes(250));

        // Добавляем элементы в менеджер
        testManager.addTask(taskItem);
        testManager.addEpic(epicItem);
        testManager.addSubTask(subTaskItem);

        // Проверяем, что в истории только стартовая запись
        Assertions.assertEquals(1, testManager.getHistory().size(), "История не пустая.");
        // Добавляем элемент в историю при помощи запроса к менеджеру
        Task taskItem1 = testManager.getTask(1);
        // проверяем, что история изменилась на 1
        Assertions.assertEquals(2, testManager.getHistory().size(), "В историю не попала запись.");
        // извлекаем из истории элемент
        Task taskItem2 = testManager.getHistory().getLast();
        // Проверяем идентичность задачи менеджера и элемента истории
        Assertions.assertEquals(taskItem1, taskItem2, "записи не соответствуют.");
        // Изменяем содержимое задачи в менеджере, при этом проверяем, что нет конфликта при взаимодействии
        // двух задач с одним id - происходит замена данных.
        taskItem = new Task(1,
                "Не работа", "Задача в работе", Status.IN_PROGRESS);
        testManager.updateTask(taskItem);
        // делаем запрос к задаче, для фиксации в истории этого запроса.
        taskItem2 = testManager.getTask(1);
        // Проверяем, что в истории есть запись для задачи 1
        Assertions.assertEquals(2, testManager.getHistory().size(), "В истории не одна запись.");
        // Сравниваем содержимое записи истории с содержимым задачи 1 до и после изменения.
        // Первая запись не должна соответствовать taskItem1, а вторая должна соответствовать taskItem2.
        Assertions.assertNotEquals(taskItem1,
                testManager.getHistory().getLast(), "записи соответствуют.");
        Assertions.assertEquals(taskItem2,
                testManager.getHistory().getLast(), "записи не соответствуют.");

    }

    @Test
    void testHistory2() {

        // Создаём элементы для добавления в менеджер
        taskItem = new Task(0,
                "Работа", "Просто задача", Status.NEW,
                LocalDateTime.of(2024, 3, 1, 10, 5, 0),
                Duration.ofMinutes(50));
        epicItem = new Epic(0,
                "Этапы", "Поэтапная работа", Status.NEW);
        subTaskItem = new SubTask(0,
                "Этап 1", "Подготовка к работе", Status.NEW, 2,
                LocalDateTime.of(2024, 1, 1, 8, 5, 0),
                Duration.ofMinutes(250));

        // Добавляем элементы в менеджер
        testManager.addTask(taskItem);
        testManager.addEpic(epicItem);
        testManager.addSubTask(subTaskItem);

        // Проверяем, что в истории только стартовая запись
        Assertions.assertEquals(1, testManager.getHistory().size(), "История не пустая.");
        // Добавляем 1 элемент в историю при помощи запроса к менеджеру
        Task taskItem1 = testManager.getTask(1);
        // проверяем, что история изменилась на 1
        Assertions.assertEquals(2,
                testManager.getHistory().size(), "В историю не попала 1 запись.");
        // Добавляем 2 элемент в историю при помощи запроса к менеджеру
        taskItem1 = testManager.getSubTask(3);
        // проверяем, что история изменилась ещё на 1
        Assertions.assertEquals(3,
                testManager.getHistory().size(), "В историю не попала 2 запись.");
        // При помощи запросов к менеджеру проверяем - повторный запросы увеличили ли количество запросов в истории.
        taskItem1 = testManager.getTask(1);
        taskItem1 = testManager.getSubTask(3);
        taskItem1 = testManager.getEpic(2);
        // Проверяем, что история осталась с 3 элементам. Что дублирования записей нет.
        Assertions.assertEquals(3,
                testManager.getHistory().size(), "В истории появились дубли.");
        // удаляем задачу и проверяем состояние истории - уменьшилась ли она, и есть ли запись с удалённой задачей.
        testManager.deleteTask(1);
        Assertions.assertEquals(2,
                testManager.getHistory().size(), "В истории не удалена запись.");
        Assertions.assertNull(testManager.getTask(1), "Задача не удалена.");
        // повторяем то-же самое с подзадачей и эпиком
        testManager.deleteSubTask(3);
        Assertions.assertEquals(1,
                testManager.getHistory().size(), "В истории не удалена запись.");
        Assertions.assertNull(testManager.getSubTask(3), "Подзадача не удалена.");
        testManager.deleteEpic(2);
        Assertions.assertEquals(0,
                testManager.getHistory().size(), "В истории не удалена запись.");
        Assertions.assertNull(testManager.getEpic(2), "Эпик не удален.");
    }
}