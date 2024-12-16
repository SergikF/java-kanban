package main.HTTPserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

// служебный класс с методами обработки запросов
class BaseHttp {
    // Получение идентификатора задачи/подзадачи/эпика
    static Optional<Integer> getId(String id) {
        try {
            return Optional.of(Integer.parseInt(id));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    // Получение и десериализация Json тела запроса -> в объекты
    static <T> T fromJsonString(HttpExchange exchange, Class<T> clazz) throws IOException {
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
    static String toJsonString(Object object) {
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

    // Отправка ответа на запрос
    static void sendResponse(HttpExchange exchange,
                                     String responseString,
                                     int responseCode) throws IOException {
        byte[] resp = responseString.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(responseCode, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

}
