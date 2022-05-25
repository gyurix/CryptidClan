package gyurix.playershops.conf;

import gyurix.cryptidcommons.db.MySQLDatabase;
import gyurix.cryptidcommons.gui.GUIConfig;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;

@Getter
public class Config {
    public List<String> categoryLore, buyLore, sellLore, buySellLore, editLore;
    public HashMap<String, GUIConfig> guis;
    public String maxRentTime;
    public String minRentTime;
    public MySQLDatabase mySQL;
    public double purchasePrice;
    public double rentPricePerDay;
    public double serverFeePercentage;
}
