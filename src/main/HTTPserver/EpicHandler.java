package main.HTTPserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.classes.Epic;
import java.io.IOException;
import java.io.InputStream;

import static main.HTTPserver.BaseHttp.*;
import static main.HTTPserver.HttpTaskServer.manager;

// метод обработки ветки /epics
// в этой ветке не проводиться проверка на пересечение интервалов времени, остальное аналогично предыдущим обработкам
class EpicHandler implements HttpHandler {
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
                    sendResponse(exchange, "Эпик добавлен !", 200);
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
