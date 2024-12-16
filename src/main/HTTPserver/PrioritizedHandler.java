package main.HTTPserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

import static main.HTTPserver.BaseHttp.*;
import static main.HTTPserver.HttpTaskServer.manager;

// метод обработки ветки /prioritized
class PrioritizedHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        sendResponse(exchange, toJsonString(manager.getPrioritizedTasks()), 200);
    }
}

