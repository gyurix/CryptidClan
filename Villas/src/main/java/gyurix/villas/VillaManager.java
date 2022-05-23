package gyurix.villas;

import gyurix.villas.data.Group;
import gyurix.villas.data.Villa;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

import static gyurix.villas.VillasPlugin.pl;
import static gyurix.villas.conf.ConfigManager.conf;
import static gyurix.villas.conf.ConfigManager.gson;

public class VillaManager {
    public static TreeMap<String, Villa> villas = new TreeMap<>();

    public static Villa getVillaAt(Location loc) {
        for (Villa v : villas.values()) {
            if (v.getArea().contains(loc))
                return v;
        }
        return null;
    }

    public static List<Villa> getVillasWithPlayer(CommandSender sender, UUID uuid) {
        boolean admin = sender.hasPermission("villas.admin");
        List<Villa> out = new ArrayList<>();
        for (Villa v : villas.values()) {
            if ((admin || v.hasPermission(sender, Group::isSee)) && v.getPlayers().containsKey(uuid))
                out.add(v);
        }
        return out;
    }

    public static void loadVillas() {
        TreeMap<String, Villa> villas = new TreeMap<>();
        Bukkit.getScheduler().runTaskAsynchronously(pl, () -> {
            conf.getMySQL().command("CREATE TABLE IF NOT EXISTS `" + conf.getMySQL().table + "` (`name` VARCHAR(32) UNIQUE PRIMARY KEY, `data` TEXT)");
            conf.getMySQL().query("SELECT `data` FROM `" + conf.getMySQL().table + "`", (rs) -> {
                while (rs.next()) {
                    Villa villa = gson.fromJson(rs.getString(1), Villa.class);
                    villas.put(villa.getName(), villa);
                }
            });
            VillaManager.villas = villas;
        });
    }

    public static void removeVilla(Villa villa) {
        villas.remove(villa.getName());
        Bukkit.getScheduler().runTaskAsynchronously(pl, () ->
                conf.getMySQL().command("DELETE FROM `" + conf.getMySQL().table + "` WHERE `name` = ? LIMIT 1", villa.getName()));
    }

    public static void saveVilla(Villa villa) {
        Bukkit.getScheduler().runTaskAsynchronously(pl, () -> {
            int updated = conf.getMySQL().update("UPDATE `" + conf.getMySQL().table + "` SET `data` = ? WHERE `name` = ?", gson.toJson(villa), villa.getName());
            if (updated == 0)
                conf.getMySQL().update("INSERT INTO `" + conf.getMySQL().table + "` VALUES ( ?, ? )", villa.getName(), gson.toJson(villa));
        });
    }
}
