package gyurix.villas;

import gyurix.villas.data.Villa;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;

import static gyurix.villas.VillasPlugin.pl;
import static gyurix.villas.conf.ConfigManager.conf;
import static gyurix.villas.conf.ConfigManager.gson;

public class VillaManager {
    public static HashMap<String, Villa> villas = new HashMap<>();

    public static Villa getVillaAt(Location to) {
        for (Villa v : villas.values()) {
            if (v.getArea().contains(to))
                return v;
        }
        return null;
    }

    public static void loadVillas() {
        HashMap<String, Villa> villas = new HashMap<>();
        Bukkit.getScheduler().runTaskAsynchronously(pl, () -> {
            conf.getMySQL().query("SELECT `data` FROM `" + conf.getMySQL().table + "`", (rs) -> {
                while (rs.next()) {
                    Villa villa = gson.fromJson(rs.getString(1), Villa.class);
                    villas.put(villa.getName(), villa);
                }
            });
            VillaManager.villas = villas;
        });
    }

    public static void saveVilla(Villa villa) {
        Bukkit.getScheduler().runTaskAsynchronously(pl, () -> {
            int updated = conf.getMySQL().update("UPDATE `" + conf.getMySQL().table + "` SET `data` = ? WHERE `name` = ?", gson.toJson(villa), villa.getName());
            if (updated == 0)
                conf.getMySQL().update("INSERT INTO `" + conf.getMySQL().table + "` VALUES ( ?, ? )", villa.getName(), gson.toJson(villa));
        });
    }
}
