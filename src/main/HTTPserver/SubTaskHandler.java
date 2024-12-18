package main.HTTPserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.classes.SubTask;

import java.io.IOException;

import static main.HTTPserver.BaseHttp.*;
import static main.HTTPserver.HttpTaskServer.manager;

// метод обработки ветки /subtasks
// обработка этой ветки аналогична обработке ветки /tasks
class SubTaskHandler implements HttpHandler {
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
                        sendResponse(exchange, "Подзадача добавлена !", 200);
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
