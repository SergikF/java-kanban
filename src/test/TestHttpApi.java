package test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import main.classes.Epic;
import main.classes.Status;
import main.classes.SubTask;
import main.classes.Task;
import main.HTTPserver.HttpTaskServer;
import main.service.InMemoryTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TestHttpApi {
    protected HttpTaskServer testserver;
    protected InMemoryTaskManager taskManager;

    @BeforeEach
    void initializeClass() throws IOException {
        // создание и запуск сервера
        taskManager = new InMemoryTaskManager();
        testserver = new HttpTaskServer(taskManager);
        testserver.run();
    }

    @AfterEach
    void shutdown() throws IOException {
        testserver.stop();
    }

    // сами тесты

    @Test
    void getAllItemsTaskAndSubTaskAndEpic() throws IOException, InterruptedException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LDTAdapter())
                .registerTypeAdapter(Duration.class, new DAdapter())
                .create(); // создаём объект Gson

        // подгружаем тестовые данные в трекер задач
        initTest(taskManager);

        // создаём HTTP-клиент
        HttpClient client = HttpClient.newHttpClient();
        // Создаём запросы на получение списков объектов и получаем ответы.
        // Получаем список задач
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        List<Task> listTasks = gson.fromJson(response.body(), new TaskTypeToken().getType());
        // Получаем список эпиков
        url = URI.create("http://localhost:8080/epics");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        List<Epic> listEpics = gson.fromJson(response.body(), new EpicTypeToken().getType());
        // Получаем список подзадач
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        List<SubTask> listSubTasks = gson.fromJson(response.body(), new SubTaskTypeToken().getType());

        // проверяем, что мы получили 3 задачи, 4 подзадачи и 2 эпика
        assertEquals(3, listTasks.size(), "Некорректное количество задач");
        assertEquals(4, listSubTasks.size(), "Некорректное количество подзадач");
        assertEquals(2, listEpics.size(), "Некорректное количество эпиков");
    }

    @Test
    void getItemsOfId_GetByIdTaskAndSubTaskAndEpic() throws IOException, InterruptedException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LDTAdapter())
                .registerTypeAdapter(Duration.class, new DAdapter())
                .create(); // создаём объект Gson

        // подгружаем тестовые данные в трекер задач
        initTest(taskManager);

        // создаём HTTP-клиент
        HttpClient client = HttpClient.newHttpClient();
        // Создаём запросы на получение объектов по их id.
        // Получаем список задач
        URI url = URI.create("http://localhost:8080/tasks/8");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        Task task = gson.fromJson(response.body(), Task.class);
        // Получаем список эпиков
        url = URI.create("http://localhost:8080/epics/5");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        Epic epic = gson.fromJson(response.body(), Epic.class);
        // Получаем список подзадач
        url = URI.create("http://localhost:8080/subtasks/9");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        SubTask subTask = gson.fromJson(response.body(), SubTask.class);

        // проверяем, что мы через API получили те-же значения, что находятся и в трекере задач
        assertEquals(task,
                taskManager.getTask(8), "Несовпадение задач API с трекером задач");
        assertEquals(subTask,
                taskManager.getSubTask(9), "Несовпадение подзадач API с трекером задач");
        assertEquals(epic,
                taskManager.getEpic(5), "Несовпадение эпиков API с трекером задач");
    }

    @Test
    void addItems_AddTaskAndSubTaskAndEpic() throws IOException, InterruptedException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LDTAdapter())
                .registerTypeAdapter(Duration.class, new DAdapter())
                .create(); // создаём объект Gson

        // создаём объекты для добавления.
        Task task = new Task(0, "Задача 1", "Добавление задачи 1",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        Epic epic = new Epic(0, "Эпик 2", "Добавление эпика 2",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        SubTask subTask = new SubTask(0, "Подзадача 3", "Добавление подзадачи 3",
                Status.NEW, 2,
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(5));

        // конвертируем их в JSON
        String taskJson = gson.toJson(task);
        String subTaskJson = gson.toJson(subTask);
        String epicJson = gson.toJson(epic);

        // создаём HTTP-клиент
        HttpClient client = HttpClient.newHttpClient();
        // Проверяем добавление задачи
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        // Проверяем добавление эпика
        url = URI.create("http://localhost:8080/epics");
        request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        // Проверяем добавление подзадачи
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что создались по одному объекту каждого типа и проверяем идентичность отправленного и полученного
        List<Task> tasksFromManager = taskManager.getAllTasks();
        assertNotNull(tasksFromManager, "Задача не создалась");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals(task.getName(),
                tasksFromManager.getFirst().getName(), "Несовпадение отправленного и полученного");
        List<SubTask> subTaskFromManager = taskManager.getAllSubTasks();
        assertNotNull(subTaskFromManager, "Подзадача не создалась");
        assertEquals(1, subTaskFromManager.size(), "Некорректное количество подзадач");
        assertEquals(subTask.getName(),
                subTaskFromManager.getFirst().getName(), "Несовпадение отправленного и полученного");
        List<Epic> epicFromManager = taskManager.getAllEpics();
        assertNotNull(epicFromManager, "Эпик не создался");
        assertEquals(1, epicFromManager.size(), "Некорректное количество эпиков");
        assertEquals(epic.getName(),
                epicFromManager.getFirst().getName(), "Несовпадение отправленного и полученного");
    }

    @Test
    void updateItemsAndStatus_updateItemsByIdAndStatusEpicByStatusSubTasks() throws IOException, InterruptedException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LDTAdapter())
                .registerTypeAdapter(Duration.class, new DAdapter())
                .create(); // создаём объект Gson

        // создаём Эпик без указания начала и конца времени выполнения и две подзадачи этого эпика.
        Epic epic = new Epic(0, "Эпик 1", "Добавление эпика 1",
                Status.NEW, null, null);
        SubTask subTask1 = new SubTask(0, "Подзадача 2", "Добавление подзадачи 2",
                Status.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(15));
        SubTask subTask2 = new SubTask(0, "Подзадача 3", "Добавление подзадачи 3",
                Status.NEW, 1, LocalDateTime.now().plusHours(1),
                Duration.ofMinutes(15));

        // конвертируем их в JSON
        String epicJson = gson.toJson(epic);
        String subTaskJson1 = gson.toJson(subTask1);
        String subTaskJson2 = gson.toJson(subTask2);

        // создаём HTTP-клиент
        HttpClient client = HttpClient.newHttpClient();
        // Добавляем эпик и задачи
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson1)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson2)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        // Проверяем, что в трекере есть один эпик (1) и две подзадачи (3 и 4). Все они имеют статус NEW
        assertEquals(Status.NEW,
                taskManager.getEpic(1).getStatus(), "Статус эпика не New");
        assertEquals(Status.NEW,
                taskManager.getSubTask(2).getStatus(), "Статус подзадачи не New");
        assertEquals(Status.NEW,
                taskManager.getSubTask(3).getStatus(), "Статус подзадачи не New");
        // Проверяем, что время старта Эпика теперь равно времени старта первой подзадачи,
        // а время завершения времени завершения второй.
        assertEquals(taskManager.getSubTask(2).getStartTime(),
                taskManager.getEpic(1).getStartTime(),
                "Время старта эпика не равно времени старта первой подзадачи");
        assertEquals(taskManager.getSubTask(3).getEndTime(),
                taskManager.getEpic(1).getEndTime(),
                "Время завершения эпика не равно времени завершения второй подзадачи");
        // Вносим изменения в подзадачу 2, не изменяя время старта и продолжительность
        subTask1 = new SubTask(2, "Изменения подзадачи 2", "Изменение подзадачи 2",
                Status.IN_PROGRESS, 1, null, null);
        subTaskJson1 = gson.toJson(subTask1);
        url = URI.create("http://localhost:8080/subtasks/2");
        request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson1)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        // Проверяем изменение названия и описания подзадачи и изменения статуса эпика на основе изменения статуса подзадачи
        assertEquals(Status.IN_PROGRESS,
                taskManager.getSubTask(2).getStatus(), "Статус подзадачи не IN_PROGRESS");
        assertEquals(Status.IN_PROGRESS,
                taskManager.getEpic(1).getStatus(), "Статус эпика не IN_PROGRESS");
        assertEquals(subTask1.getName(),
                taskManager.getSubTask(2).getName(), "Название подзадачи 2 не изменилось");
        // Изменяем статусы подзадач на Done без изменения названий, и изменяем время второй подзадачи.
        subTask1 = new SubTask(2, null, null,
                Status.DONE, 1, null, null);
        subTask2 = new SubTask(3, null, null,
                Status.DONE, 1, LocalDateTime.now().plusHours(3),
                Duration.ofMinutes(30));
        subTaskJson1 = gson.toJson(subTask1);
        request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson1)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        subTaskJson2 = gson.toJson(subTask2);
        request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson2)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        // Проверяем изменения статусов подзадач и эпика,
        // и соотвествие времени начала и завершения эпика, согласно новым данным подзадачи 3
        assertEquals(Status.DONE,
                taskManager.getSubTask(2).getStatus(), "Статус подзадачи не DONE");
        assertEquals(Status.DONE,
                taskManager.getSubTask(3).getStatus(), "Статус подзадачи не DONE");
        assertEquals(Status.DONE,
                taskManager.getEpic(1).getStatus(), "Статус эпика не DONE");
        assertEquals(taskManager.getSubTask(2).getStartTime(),
                taskManager.getEpic(1).getStartTime(),
                "Время старта эпика не равно времени старта первой подзадачи");
        assertEquals(taskManager.getSubTask(3).getEndTime(),
                taskManager.getEpic(1).getEndTime(),
                "Время завершения эпика не равно времени завершения второй подзадачи");
    }

    @Test
    void checkingOverlaysItems_ifTimeOccupiedByTask_cannotAddTaskThisTime() throws IOException, InterruptedException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LDTAdapter())
                .registerTypeAdapter(Duration.class, new DAdapter())
                .create(); // создаём объект Gson

        // создаём объекты для добавления.
        Task task1 = new Task(0, "Задача 1", "Добавление задачи 1",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(120));
        SubTask subTask = new SubTask(0, "Подзадача 2", "Добавление подзадачи 2",
                Status.NEW, 2, LocalDateTime.now().plusHours(6),
                Duration.ofMinutes(30));
        Task task2 = new Task(0, "Задача 3", "Добавление задачи 3",
                Status.NEW, LocalDateTime.now().plusHours(1),
                Duration.ofMinutes(15));

        // конвертируем их в JSON
        String taskJson1 = gson.toJson(task1);
        String taskJson2 = gson.toJson(task2);
        String subTaskJson = gson.toJson(subTask);

        // создаём HTTP-клиент
        HttpClient client = HttpClient.newHttpClient();
        // Проверяем добавление задачи 1
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson1)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // Проверяем добавление подзадачи 2 - время не пересекается с временем задачи 1
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // Проверяем добавление задачи 3 - тут время пересекается и код ответа должен быть 406
        url = URI.create("http://localhost:8080/tasks");
        request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(406, response.statusCode());

        // Вносим изменения времени в подзадачу и тоже получаем ответ 406
        subTask = new SubTask(2, "Изменения 2", "Изменения времени подзадачи 2",
                Status.IN_PROGRESS, 2, LocalDateTime.now().plusMinutes(30),
                Duration.ofMinutes(15));
        subTaskJson = gson.toJson(subTask);
        url = URI.create("http://localhost:8080/subtasks/2");
        request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(406, response.statusCode());
    }

    @Test
    void deleteItems_DeleteTaskAndSubTaskAndEpics_correctlyDelete() throws IOException, InterruptedException {
        // подгружаем тестовые данные в трекер задач
        initTest(taskManager);

        // создаём HTTP-клиент
        HttpClient client = HttpClient.newHttpClient();
        // Создаём запросы на удаление объектов по их id.
        URI url = URI.create("http://localhost:8080/tasks/8");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        url = URI.create("http://localhost:8080/epics/5");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        url = URI.create("http://localhost:8080/subtasks/9");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // Создаём запросы на удаление объектов по их несуществующим id и получаем ответ 404
        url = URI.create("http://localhost:8080/tasks/18");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode());
        url = URI.create("http://localhost:8080/epics/15");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode());
        url = URI.create("http://localhost:8080/subtasks/19");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode());

        // проверяем, что количество задач, подзадач и эпиков уменьшилось согласно нашим действиям
        // Задачи было 3 - станет 2
        // Подзадачи было 4 - станет 3 из-за удаления подзадачи
        // Эпики было 2 - станет 1, но вместе с эпиком удалились 2 подзадачи, значит подзадач станет 1
        assertEquals(2, taskManager.getAllTasks().size(), "Неправильное количество задач");
        assertEquals(1, taskManager.getAllSubTasks().size(), "Неправильное количество подзадач");
        assertEquals(1, taskManager.getAllEpics().size(), "Неправильное количество эпиков");

    }

    @Test
    void getHistory_correctAddHistoryAndDeleteHistoryByDeleteTask() throws IOException, InterruptedException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LDTAdapter())
                .registerTypeAdapter(Duration.class, new DAdapter())
                .create(); // создаём объект Gson

        // подгружаем тестовые данные в трекер задач
        initTest(taskManager);

        // создаём HTTP-клиент
        HttpClient client = HttpClient.newHttpClient();

        // Получаем историю действий
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        List<Task> listHistory = gson.fromJson(response.body(), new TaskTypeToken().getType());
        // проверяем, что в истории 2 записи при инициализации трекера задач
        assertEquals(2, listHistory.size(), "История не пуста");

        // Делаем 3 запроса на получение объекта по его id
        url = URI.create("http://localhost:8080/tasks/8");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        url = URI.create("http://localhost:8080/subtasks/9");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        // Получаем историю действий
        url = URI.create("http://localhost:8080/history");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        listHistory = gson.fromJson(response.body(), new TaskTypeToken().getType());
        // проверяем, что в истории плюс 2 позиции - 5
        assertEquals(4, listHistory.size(), "В истории некорректное количество записей");

        // удаляем задачу, по которой был запрос и проверяем историю -
        // удалённая задача должна выпасть из истории и там должно быть 2 записи.
        url = URI.create("http://localhost:8080/tasks/8");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        url = URI.create("http://localhost:8080/history");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        listHistory = gson.fromJson(response.body(), new TaskTypeToken().getType());
        // проверяем, что в истории 3 позиции
        assertEquals(3, listHistory.size(), "В истории некорректное количество записей");
    }

    @Test
    void getPrioritized_correctCreateListPriorityAndAutoReordering() throws IOException, InterruptedException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LDTAdapter())
                .registerTypeAdapter(Duration.class, new DAdapter())
                .create(); // создаём объект Gson

        // создаём объекты для добавления.
        Task task1 = new Task(0, "Задача 1", "Добавление задачи 1",
                Status.NEW, LocalDateTime.now().plusHours(2),
                Duration.ofMinutes(15));
        Task task2 = new Task(0, "Задача 2", "Добавление задачи 2",
                Status.NEW, LocalDateTime.now().plusHours(1),
                Duration.ofMinutes(15));
        Epic epic3 = new Epic(0, "Эпик 3", "Добавление эпика 3",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        SubTask subTask4 = new SubTask(0, "Подзадача 4", "Добавление подзадачи 4",
                Status.NEW, 3, LocalDateTime.now().plusHours(3),
                Duration.ofMinutes(25));
        SubTask subTask5 = new SubTask(0, "Подзадача 5", "Добавление подзадачи 5",
                Status.NEW, 3, LocalDateTime.now(), Duration.ofMinutes(35));

        // конвертируем их в JSON
        String taskJson1 = gson.toJson(task1);
        String taskJson2 = gson.toJson(task2);
        String epicJson3 = gson.toJson(epic3);
        String subTaskJson4 = gson.toJson(subTask4);
        String subTaskJson5 = gson.toJson(subTask5);

        // создаём HTTP-клиент
        HttpClient client = HttpClient.newHttpClient();

        // делаем запрос списка приоритета задач
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        List<Task> listPrioritized = gson.fromJson(response.body(), new TaskTypeToken().getType());
        // проверяем, что в список пуст
        assertEquals(0, listPrioritized.size(), "Список приоритета не пуст");

        // Добавляем три элемента
        url = URI.create("http://localhost:8080/tasks");
        request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson1)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        url = URI.create("http://localhost:8080/tasks");
        request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        url = URI.create("http://localhost:8080/epics");
        request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson3)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson4)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

// Снова запрашиваем список приоритета задач и проверяем что в нём 3 позиции
        url = URI.create("http://localhost:8080/prioritized");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        listPrioritized = gson.fromJson(response.body(), new TaskTypeToken().getType());
        assertEquals(3, listPrioritized.size(), "Список не верен");
        // Проверяем правильность позиций списка
        assertEquals(2, listPrioritized.get(0).getId(), "Первая задача не верна");
        assertEquals(1, listPrioritized.get(1).getId(), "Вторая задача не верна");
        assertEquals(4, listPrioritized.get(2).getId(), "Третья задача не верна");

        // Добавляем ещё одну подзадачу и снова проверяем список и порядок в списке
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson5)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        url = URI.create("http://localhost:8080/prioritized");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        listPrioritized = gson.fromJson(response.body(), new TaskTypeToken().getType());
        assertEquals(4, listPrioritized.size(), "Список не верен");
        // Проверяем правильность позиций списка
        assertEquals(5, listPrioritized.get(0).getId(), "Первая задача не верна");
        assertEquals(2, listPrioritized.get(1).getId(), "Первая задача не верна");
        assertEquals(1, listPrioritized.get(2).getId(), "Вторая задача не верна");
        assertEquals(4, listPrioritized.get(3).getId(), "Третья задача не верна");

        // Удаляем эпик, а вместе с ним и 2 подзадачи. И снова проверяем список и порядок в списке
        url = URI.create("http://localhost:8080/epics/3");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        url = URI.create("http://localhost:8080/prioritized");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        listPrioritized = gson.fromJson(response.body(), new TaskTypeToken().getType());
        assertEquals(2, listPrioritized.size(), "Список не верен");
        // Проверяем правильность позиций списка
        assertEquals(2, listPrioritized.get(0).getId(), "Первая задача не верна");
        assertEquals(1, listPrioritized.get(1).getId(), "Вторая задача не верна");
    }


    void initTest(InMemoryTaskManager manager) {
        manager.addTask(new Task(0,
                "Работа", "Просто задача", Status.NEW,
                LocalDateTime.of(2024, 1, 1, 10, 5, 0),
                Duration.ofMinutes(50)));
        manager.addEpic(new Epic(0,
                "Этапы", "Поэтапная работа", Status.NEW));
        manager.addSubTask(new SubTask(0,
                "Этап 1", "Подготовка к работе", Status.NEW, 2,
                LocalDateTime.of(2024, 1, 1, 8, 0, 0),
                Duration.ofHours(23)));
        manager.addTask(new Task(0,
                "Отдых", "Поехать в отпуск", Status.NEW));
        manager.addEpic(new Epic(0,
                "Обед", "Нужно утолить голод", Status.NEW));
        manager.addSubTask(new SubTask(0,
                "Закупка", "Надо в магазин", Status.NEW, 5,
                LocalDateTime.of(2024, 1, 8, 9, 0, 0),
                Duration.ofHours(6)));
        manager.addSubTask(new SubTask(0,
                "Приготовление", "Немного постоять у плиты", Status.NEW, 5,
                LocalDateTime.of(2024, 1, 6, 11, 0, 0),
                Duration.ofHours(1)));
        manager.addTask(new Task(0,
                "Попытка 5", "Новая задача после загрузки", Status.NEW,
                LocalDateTime.of(2024, 1, 6, 11, 40, 0),
                Duration.ofHours(1)));
        manager.addSubTask(new SubTask(0,
                "Попытка 6", "Новая подзадача после загрузки", Status.NEW, 2,
                LocalDateTime.of(2024, 1, 1, 10, 50, 0),
                Duration.ofHours(1)));
    }

    // Адаптер для LocalDateTime
    static class LDTAdapter extends TypeAdapter<LocalDateTime> {
        private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss.SSS");

        @Override
        public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
            if (localDateTime != null) {
                jsonWriter.value(localDateTime.format(dtf));
            } else {
                jsonWriter.nullValue();
            }
        }

        @Override
        public LocalDateTime read(final JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() != JsonToken.NULL) {
                return LocalDateTime.parse(jsonReader.nextString(), dtf);
            } else {
                jsonReader.nextNull();
                return null;
            }
        }
    }

    // Адаптер для Duration
    static class DAdapter extends TypeAdapter<Duration> {
        @Override
        public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
            if (duration != null) {
                jsonWriter.value(duration.toString());
            } else {
                jsonWriter.nullValue();
            }
        }

        @Override
        public Duration read(final JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() != JsonToken.NULL) {
                return Duration.parse(jsonReader.nextString());
            } else {
                jsonReader.nextNull();
                return null;
            }
        }
    }

    // для разбора JSON
    static class TaskTypeToken extends TypeToken<List<Task>> {
    }

    static class SubTaskTypeToken extends TypeToken<List<SubTask>> {
    }

    static class EpicTypeToken extends TypeToken<List<Epic>> {
    }

}
