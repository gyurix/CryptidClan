package gyurix.playershops.data;

import java.util.HashMap;

public class ShopItem {
    public double accumulatedWRLD;
    public double buyPrice;
    public boolean buyable;
    public String categoryName;
    public String item;
    public double sellPrice;
    public boolean sellable;
    public HashMap<Integer, ShopItem> subItems;
    public int supply;

}
