package main.HTTPserver;

import com.sun.net.httpserver.HttpServer;
import main.classes.Epic;
import main.classes.Status;
import main.classes.SubTask;
import main.classes.Task;
import main.service.InMemoryTaskManager;
import main.service.Managers;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {

    // создаём рабочие экземпляры менеджера задач и сервера
    static InMemoryTaskManager manager;
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
    }

    // метод для остановки сервера из вне класса HttpTaskServer
    public void stop() throws IOException {
        server.stop(0);
    }

    static void initTest() {
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