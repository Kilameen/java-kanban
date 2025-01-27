package tracker.server.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime value) throws IOException {
        if (value == null) {
            jsonWriter.nullValue(); // Используем nullValue для null
        } else {
            jsonWriter.value(value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)); // Записываем дату в формате ISO
        }
    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == com.google.gson.stream.JsonToken.NULL) {
            jsonReader.nextNull();
            return null; // Возвращаем null, если значение null
        }
        return LocalDateTime.parse(jsonReader.nextString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME); // Парсим строку в LocalDateTime
    }
}