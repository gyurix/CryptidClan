package gyurix.playershops.data;

import gyurix.cryptidcommons.util.ItemUtils;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

import static gyurix.cryptidcommons.util.StrUtils.DF;
import static gyurix.playershops.conf.ConfigManager.conf;

public class ShopItem {
    public double accumulatedWRLD;
    public double buyPrice;
    public boolean buyable;
    public String categoryName = "";
    public ItemStack item;
    public double sellPrice;
    public boolean sellable;
    public HashMap<Integer, ShopItem> subItems = new HashMap<>();
    public int supply;

    public int countCategories() {
        return (int) subItems.values().stream().filter(shopItem -> shopItem.categoryName != null).count();
    }

    public int countItems() {
        if (categoryName == null)
            return 1;
        int items = 0;
        for (ShopItem shopItem : subItems.values())
            items += shopItem.countItems();
        return items;
    }

    public ItemStack getDisplayItem(boolean admin) {
        if (!admin && !buyable && !sellable)
            return conf.guis.get("shop").getStaticItem("glass");
        List<String> lore = admin ? conf.editLore : buyable ? sellable ? conf.buySellLore : conf.buyLore : conf.sellLore;
        return ItemUtils.addLore(item.clone(), lore,
                "allowBuy", buyable ? "§ayes" : "§cno",
                "maxBuy", supply,
                "buyPrice", DF.format(buyPrice),
                "allowSell", sellable ? "§ayes" : "§cno",
                "maxSell", DF.format((int) accumulatedWRLD / (sellPrice * (1 + conf.serverFeePercentage / 100.0))),
                "sellPrice", DF.format(sellPrice),
                "stored", DF.format(accumulatedWRLD),
                "items", supply);
    }

    public double removeFrom(PlayerShop shop) {
        if (categoryName == null) {
            shop.getOverflow().put(item, shop.getOverflow().getOrDefault(item, 0) + supply);
            return 0;
        }
        double sum = 0;
        for (ShopItem value : subItems.values()) {
            sum += value.removeFrom(shop);
        }
        return sum;
    }
}
