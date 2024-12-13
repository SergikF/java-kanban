package main.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import main.classes.Epic;
import main.classes.Status;
import main.classes.SubTask;
import main.classes.Task;


import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class HttpTaskServer {

    // создаём рабочие экземпляры менеджера задач и сервера
    private static InMemoryTaskManager manager;
    private static HttpServer server;

    // создаём конструктор на тот случай, если класс будет запускаться не из самого класса, а созданием экземпляра
    public HttpTaskServer(InMemoryTaskManager manager) {
        this.manager = manager;
    }

    public static void main(String[] args) throws IOException {

        // две заготовки для запуска внутри класса ...

        //  Создаём менеджер задач на файловой основе
        //File testFile;
        //try {
        //    testFile = File.createTempFile("test", ".csv");
        //} catch (IOException e) {
        //    throw new ManagerSaveException("Не удалось создать временный файл");
        //}
        //TaskManager taskManagerAPI = Managers.getFile(testFile);

        //  Создаём менеджер задач в памяти
        HttpTaskServer taskmanager = new HttpTaskServer(Managers.getDefault());
        // подгружаем тестовые данные
        initTest();
        // запускаем сервер
        taskmanager.run();

    }

    // метод для запуска сервера из вне класса HttpTaskServer
    public void run() throws IOException {
        // настройка и запуск HTTP-сервера
        server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/tasks", new TasksHandler());
        server.createContext("/subtasks", new SubTaskHandler());
        server.createContext("/epics", new EpicHandler());
        server.createContext("/history", new HistoryHandler());
        server.createContext("/prioritized", new PrioritizedHandler());
        server.start(); // запускаем сервер

        System.out.println("HTTP-сервер запущен на 8080 порту!");
    }

    // метод для остановки сервера из вне класса HttpTaskServer
    public void stop() throws IOException {
        server.stop(0);
    }

    // метод обработки ветки /tasks
    private static class TasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // получаем метод и парсим строку запроса в массив
            String[] path = exchange.getRequestURI().getPath().split("/");
            String metod = exchange.getRequestMethod();
            // разделение на методы
            switch (metod) {
                case "GET":
                    // если в запросе только /tasks - отдаём все задачи
                    if (path.length <= 2) {
                        sendResponse(exchange,
                                toJsonString(manager.getAllTasks()),
                                200);
                    } else {
                        // если в запросе есть id - отдаём задачу по id,
                        // но только если преобразование id в int не приводит к ошибке
                        if (getId(path[2]).isPresent()) {
                            Task task = manager.getTask(getId(path[2]).get());
                            // если с цифрой id нет проблем - то пробуем получить задачу по id
                            // если успешно - отдаём задачу, если нет - сообщаем
                            if (task != null) {
                                sendResponse(exchange,
                                    toJsonString(manager.getTask(getId(path[2]).get())),
                                    200);
                            } else {
                                sendResponse(exchange,
                                    "Задачи с id = " + path[2] + " не существует !",
                                    404);
                            }
                            break;
                        }
                        break;
                    }
                    break;
                case "POST":
                    // преобразуем приходящий в теле запроса Json в объект Task
                    Task task = fromJsonString(exchange, Task.class);
                    // если в запросе только /tasks - добавляем задачу,
                    // предварительно проверяя пересечение интервалов времени
                    if (path.length <= 2) {
                        if (!manager.isOverlay(task)) {
                            manager.addTask(task);
                            sendResponse(exchange, "Задача добавлена !", 201);
                        } else {
                            sendResponse(exchange,
                                "Задача по времени выполнения пересекается с существующими задачами !",
                                406);
                        }
                        break;
                        // если в запросе больше чем только /tasks
                        // проверяем - преобразование id в int не приводит к ошибке
                    } else if (getId(path[2]).isPresent()) {
                        // если с цифрой id нет проблем - то пробуем обновить задачу по id,
                        // предварительно проверяя пересечение интервалов времени,
                        // если успешно - обновляем задачу, если нет - сообщаем
                        if (!manager.isOverlay(task)) {
                            if (manager.updateTask(task)) {
                                sendResponse(exchange, "Задача с id = " + task.getId() + " обновлена !",
                                        201);
                            } else {
                                sendResponse(exchange, "Задачи с id = " + path[2] + " не существует !",
                                        404);
                            }
                        } else {
                            sendResponse(exchange,
                                "Задача по времени выполнения пересекается с существующими задачами !",
                                406);
                            break;
                        }
                        break;
                    }
                    break;
                case "DELETE":
                    // если для метода /delete есть id - удаляем задачу по id,
                    // предварительно проверяя правильность перевода id в int и наличие задачи по id
                    if (path.length > 2) {
                        if (getId(path[2]).isPresent()) {
                            if (manager.deleteTask(getId(path[2]).get())) {
                                sendResponse(exchange, "Задача с id = " + path[2] + " удалена !",
                                        200);
                            } else {
                                sendResponse(exchange, "Задачи с id = " + path[2] + " не существует !",
                                        404);
                            }
                            break;
                        }
                        break;
                    }
                    break;
            }
            // если в методах в строках запроса передаётся некорректные данные - отправляем ошибку
            sendResponse(exchange, "Ошибка в запросе метода !", 400);
        }
    }

    // метод обработки ветки /subtasks
    // обработка этой ветки аналогична обработке ветки /tasks
    private static class SubTaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String[] path = exchange.getRequestURI().getPath().split("/");
            String metod = exchange.getRequestMethod();
            switch (metod) {
                case "GET":
                    if (path.length <= 2) {
                        sendResponse(exchange,
                                toJsonString(manager.getAllSubTasks()),
                                200);
                    } else {
                        if (getId(path[2]).isPresent()) {
                            SubTask subtask = manager.getSubTask(getId(path[2]).get());
                            if (subtask != null) {
                                sendResponse(exchange,
                                    toJsonString(manager.getSubTask(getId(path[2]).get())),
                                    200);
                            } else {
                                sendResponse(exchange,
                                    "Подзадачи с id = " + path[2] + " не существует !",
                                    404);
                            }
                            break;
                        }
                        break;
                    }
                    break;
                case "POST":
                    SubTask task = fromJsonString(exchange, SubTask.class);
                    if (path.length <= 2) {
                        if (!manager.isOverlay(task)) {
                            manager.addSubTask(task);
                            sendResponse(exchange, "Подзадача добавлена !", 201);
                        } else {
                            sendResponse(exchange,
                               "Подзадача по времени выполнения пересекается с существующими задачами !",
                               406);
                        }
                        break;
                    } else if (getId(path[2]).isPresent()) {
                        if (!manager.isOverlay(task)) {
                            if (manager.updateSubTask(task)) {
                                sendResponse(exchange,
                                        "Подзадача с id = " + task.getId() + " обновлена !",
                                        201);
                            } else {
                                sendResponse(exchange,
                                        "Подзадачи с id = " + path[2] + " не существует !",
                                        404);
                            }
                        } else {
                            sendResponse(exchange,
                               "Подзадача по времени выполнения пересекается с существующими задачами !",
                               406);
                            break;
                        }
                        break;
                    }
                    break;
                case "DELETE":
                    if (path.length > 2) {
                        if (getId(path[2]).isPresent()) {
                            if (manager.deleteSubTask(getId(path[2]).get())) {
                                sendResponse(exchange,
                                   "Подзадача с id = " + path[2] + " удалена !", 200);
                            } else {
                                sendResponse(exchange,
                                   "Подзадачи с id = " + path[2] + " не существует !", 404);
                            }
                            break;
                        }
                        break;
                    }
                    break;
            }
            sendResponse(exchange, "Ошибка в запросе метода !", 400);
        }
    }

    // метод обработки ветки /epics
    // в этой ветке не проводиться проверка на пересечение интервалов времени, остальное аналогично предыдущим обработкам
    private static class EpicHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String[] path = exchange.getRequestURI().getPath().split("/");
            String metod = exchange.getRequestMethod();
            InputStream body = exchange.getRequestBody();
            switch (metod) {
                case "GET":
                    if (path.length <= 2) {
                        sendResponse(exchange, toJsonString(manager.getAllEpics()), 200);
                    } else {
                        if (getId(path[2]).isPresent()) {
                            Epic epic = manager.getEpic(getId(path[2]).get());
                            if (epic != null) {
                                if (path.length == 3) {
                                    sendResponse(exchange,
                                      toJsonString(manager.getEpic(getId(path[2]).get())),
                                      200);
                                } else if (path[3].equals("subtasks")) {
                                    sendResponse(exchange,
                                      toJsonString(manager.getEpicSubTasks(getId(path[2]).get())),
                                      200);
                                }
                            } else {
                                sendResponse(exchange,
                                        "Эпика с id = " + path[2] + " не существует !", 404);
                            }
                            break;
                        }
                        break;
                    }
                    break;
                case "POST":
                    Epic task = fromJsonString(exchange, Epic.class);
                    if (path.length <= 2) {
                        manager.addEpic(task);
                        sendResponse(exchange, "Эпик добавлен !", 201);
                        break;
                    } else if (getId(path[2]).isPresent()) {
                        if (manager.updateEpic(task)) {
                            sendResponse(exchange,
                                    "Эпик с id = " + task.getId() + " обновлен !", 201);
                        } else {
                            sendResponse(exchange,
                                    "Эпик с id = " + path[2] + " не существует !", 404);
                        }
                        break;
                    }
                    break;
                case "DELETE":
                    if (path.length > 2) {
                        if (getId(path[2]).isPresent()) {
                            if (manager.deleteEpic(getId(path[2]).get())) {
                                sendResponse(exchange,
                                        "Эпик с id = " + path[2] + " удален !", 200);
                            } else {
                                sendResponse(exchange,
                                        "Эпика с id = " + path[2] + " не существует !", 404);
                            }
                            break;
                        }
                        break;
                    }
                    break;
            }
            sendResponse(exchange, "Ошибка в запросе метода !", 400);
        }
    }

    // метод обработки ветки /history
    private static class HistoryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendResponse(exchange, toJsonString(manager.getHistory()), 200);
        }
    }

    // метод обработки ветки /prioritized
    private static class PrioritizedHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendResponse(exchange, toJsonString(manager.getPrioritizedTasks()), 200);
        }
    }

    // Получение идентификатора задачи/подзадачи/эпика
    private static Optional<Integer> getId(String id) {
        try {
            return Optional.of(Integer.parseInt(id));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    // Получение и десериализация Json тела запроса -> в объекты
    private static <T> T fromJsonString(HttpExchange exchange, Class<T> clazz) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LDTAdapter())
                .registerTypeAdapter(Duration.class, new DAdapter())
                .create();
        return gson.fromJson(body, clazz);
    }

    // Сериализация объекта в JSON
    private static String toJsonString(Object object) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LDTAdapter())
                .registerTypeAdapter(Duration.class, new DAdapter())
                .create();
        return gson.toJson(object);
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
                return null;
            }
        }
    }

    // Отправка ответа на запрос
    private static void sendResponse(HttpExchange exchange,
                                     String responseString,
                                     int responseCode) throws IOException {
        byte[] resp = responseString.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(responseCode, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    private static void initTest() {
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
}