package main.HTTPserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.classes.Task;

import java.io.IOException;

import static main.HTTPserver.BaseHttp.*;
import static main.HTTPserver.HttpTaskServer.manager;

// метод обработки ветки /tasks
class TasksHandler implements HttpHandler {

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
                    BaseHttp.sendResponse(exchange,
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
                        sendResponse(exchange, "Задача добавлена !", 200);
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
