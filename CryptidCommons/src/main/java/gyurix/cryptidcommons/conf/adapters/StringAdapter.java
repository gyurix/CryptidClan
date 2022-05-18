package gyurix.cryptidcommons.conf.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import gyurix.cryptidcommons.util.StrUtils;

import java.io.IOException;

public class StringAdapter extends TypeAdapter<String> {
    @Override
    public String read(JsonReader jsonReader) throws IOException {
        return StrUtils.colorize(jsonReader.nextString());
    }

    @Override
    public void write(JsonWriter jsonWriter, String value) throws IOException {
        jsonWriter.value(StrUtils.decolorize(value));
    }
}
