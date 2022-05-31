package gyurix.villas.conf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gyurix.cryptidcommons.conf.Messages;
import gyurix.cryptidcommons.conf.adapters.ItemStackAdapter;
import gyurix.cryptidcommons.conf.adapters.PostProcessableAdapter;
import gyurix.cryptidcommons.conf.adapters.StringAdapter;
import gyurix.cryptidcommons.conf.adapters.StringSerializableAdapter;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileReader;

import static gyurix.villas.VillasPlugin.pl;

public class ConfigManager {
    public static Config conf;
    public static File confFile = new File(pl.getDataFolder() + File.separator + "config.json"),
            msgFile = new File(pl.getDataFolder() + File.separator + "messages.yml");
    public static Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(new StringSerializableAdapter())
            .registerTypeAdapterFactory(new PostProcessableAdapter())
            .registerTypeAdapterFactory(new ItemStackAdapter())
            .registerTypeAdapter(String.class, new StringAdapter().nullSafe())
            .serializeNulls()
            .setPrettyPrinting()
            .create();
    public static Messages msg;

    @SneakyThrows
    public static void reload() {
        if (!confFile.exists())
            pl.saveResource("config.json", false);

        if (!msgFile.exists())
            pl.saveResource("messages.yml", false);

        try (FileReader confFileReader = new FileReader(confFile)) {
            conf = gson.fromJson(confFileReader, Config.class);
            msg = new Messages(YamlConfiguration.loadConfiguration(msgFile));
        }
    }
}
