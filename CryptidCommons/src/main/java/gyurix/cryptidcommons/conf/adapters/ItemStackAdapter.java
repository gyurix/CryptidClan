package gyurix.cryptidcommons.conf.adapters;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import gyurix.cryptidcommons.util.ItemUtils;
import lombok.SneakyThrows;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public class ItemStackAdapter implements TypeAdapterFactory {
    @SneakyThrows
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        if (ItemStack.class.isAssignableFrom(typeToken.getRawType())) {
            return new TypeAdapter<T>() {
                @Override
                public T read(JsonReader jsonReader) throws IOException {
                    return (T) ItemUtils.stringToItemStack(jsonReader.nextString());
                }

                @Override
                public void write(JsonWriter jsonWriter, T item) throws IOException {
                    jsonWriter.value(ItemUtils.itemToString((ItemStack) item));
                }
            }.nullSafe();
        }
        return null;
    }
}
