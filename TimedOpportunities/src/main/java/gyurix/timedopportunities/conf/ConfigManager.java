package gyurix.timedopportunities.conf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import gyurix.cryptidcommons.conf.Messages;
import gyurix.cryptidcommons.conf.adapters.ItemStackAdapter;
import gyurix.cryptidcommons.conf.adapters.PostProcessableAdapter;
import gyurix.cryptidcommons.conf.adapters.StringAdapter;
import gyurix.cryptidcommons.conf.adapters.StringSerializableAdapter;
import gyurix.cryptidcommons.data.BlockLoc;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;

import static gyurix.timedopportunities.TimedOpportunitiesPlugin.pl;

public class ConfigManager {
    public static Config conf;
    public static File confFile = new File(pl.getDataFolder() + File.separator + "config.json"),
            locFile = new File(pl.getDataFolder() + File.separator + "locations.json"),
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

        if (!locFile.exists())
            pl.saveResource("locations.json", false);

        if (!msgFile.exists())
            pl.saveResource("messages.yml", false);

        try (FileReader confFileReader = new FileReader(confFile); FileReader locFileReader = new FileReader(locFile)) {
            conf = gson.fromJson(confFileReader, Config.class);
            conf.locations = gson.fromJson(locFileReader, TypeToken.getParameterized(List.class, BlockLoc.class).getType());
            msg = new Messages(YamlConfiguration.loadConfiguration(msgFile));
        }
    }

    @SneakyThrows
    public static void saveLocations() {
        try (FileWriter fileWriter = new FileWriter(locFile)) {
            gson.toJson(conf.locations, fileWriter);
        }
    }
}
