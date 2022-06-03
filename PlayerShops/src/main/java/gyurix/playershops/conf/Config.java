package gyurix.playershops.conf;

import gyurix.cryptidcommons.db.MySQLDatabase;
import gyurix.cryptidcommons.gui.GUIConfig;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public class Config {
    public TreeMap<String, Double> categoryLicenseCosts;
    public HashMap<Material, String> categoryLicenses;
    public LinkedHashMap<String, Integer> categoryLimits, itemLimits;
    public List<String> categoryLore, buyLore, sellLore, buySellLore, editLore, editCategoryLore;
    public HashMap<String, GUIConfig> guis;
    public String maxRentTime;
    public String minRentTime;
    public MySQLDatabase mySQL;
    public double purchasePrice;
    public double rentPricePerDay;
    public double serverFeePercentage;

    public int getCategoryLimit(Player plr) {
        for (Map.Entry<String, Integer> e : categoryLimits.entrySet()) {
            if (plr.hasPermission("playershops.categorylimit." + e.getKey()))
                return e.getValue();
        }
        return categoryLimits.getOrDefault("default", 0);
    }

    public int getItemLimit(Player plr) {
        for (Map.Entry<String, Integer> e : itemLimits.entrySet()) {
            if (plr.hasPermission("playershops.itemlimit." + e.getKey()))
                return e.getValue();
        }
        return itemLimits.getOrDefault("default", 0);
    }
}
