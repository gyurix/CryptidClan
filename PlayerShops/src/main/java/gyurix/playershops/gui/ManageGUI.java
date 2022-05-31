package gyurix.playershops.gui;

import gyurix.cryptidcommons.gui.CustomGUI;
import gyurix.cryptidcommons.util.ItemUtils;
import gyurix.playershops.PlayerShopManager;
import gyurix.playershops.data.PlayerShop;
import gyurix.playershops.data.ShopItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static gyurix.cryptidcommons.util.StrUtils.DF;
import static gyurix.playershops.conf.ConfigManager.conf;
import static gyurix.playershops.conf.ConfigManager.msg;

public class ManageGUI extends CustomGUI {
    private final ShopItem parent;
    private final PlayerShop shop;
    private final ShopItem shopItem;

    public ManageGUI(Player plr, PlayerShop shop, ShopItem shopItem, ShopItem parent) {
        super(plr, conf.guis.get("manage"));
        this.parent = parent;
        this.shop = shop;
        this.shopItem = shopItem;
        open("player", shop.getOwnerName(), "category", ItemUtils.getName(shopItem.item));
    }

    @Override
    public ItemStack getCustomItem(String name) {
        switch (name) {
            case "item" -> {
                return shopItem.item;
            }
            case "buyable" -> {
                return config.getCustomItem(shopItem.buyable ? "buyableYes" : "buyable");
            }
            case "buyPrice" -> {
                return config.getCustomItem("buyPrice",
                        "price", DF.format(shopItem.buyPrice),
                        "tax", DF.format(shopItem.buyPrice * conf.serverFeePercentage / 100.0),
                        "taxPct", DF.format(conf.serverFeePercentage),
                        "netPrice", DF.format(shopItem.buyPrice * (1 - conf.serverFeePercentage / 100.0)));
            }
            case "depositItems" -> {
                return config.getCustomItem("depositItems", "stock", shopItem.stock);
            }
            case "withdrawItems" -> {
                return config.getCustomItem("withdrawItems", "stock", shopItem.stock);
            }
            case "sellable" -> {
                return config.getCustomItem(shopItem.sellable ? "sellableYes" : "sellable");
            }
            case "sellPrice" -> {
                return config.getCustomItem("sellPrice",
                        "price", DF.format(shopItem.sellPrice),
                        "tax", DF.format(shopItem.sellPrice * conf.serverFeePercentage / 100.0),
                        "taxPct", DF.format(conf.serverFeePercentage),
                        "netPrice", DF.format(shopItem.sellPrice * (1 - conf.serverFeePercentage / 100.0)));
            }
            case "depositWrld" -> {
                return config.getCustomItem("depositWrld", "stored", DF.format(shopItem.accumulatedWRLD));
            }
            case "withdrawWrld" -> {
                return config.getCustomItem("withdrawWrld", "stored", DF.format(shopItem.accumulatedWRLD));
            }
        }
        throw new RuntimeException("Unknown custom item " + name);
    }

    @Override
    public void onClick(int slot, boolean right, boolean shift) {
        if (slot >= inv.getSize() || slot < 0)
            return;
        String type = config.getLayout().get(slot);
        switch (type) {
            case "back" -> new ShopGUI(plr, shop, true, parent);
            case "delete" -> {
                shopItem.removeFrom(shop);
                msg.msg(plr, "delete.item", "item", ItemUtils.getName(shopItem.item));
                new ShopGUI(plr, shop, true, parent);
            }
            case "exit" -> plr.closeInventory();
            case "buyable" -> {
                shopItem.buyable = !shopItem.buyable;
                PlayerShopManager.save(shop);
                update();
                msg.msg(plr, "item.buyable." + (shopItem.buyable ? "enable" : "disable"), "item", ItemUtils.getName(shopItem.item));
            }
            case "buyPrice" -> shopItem.changeBuyPrice(this, shop, plr);
            case "depositItems" -> shopItem.depositItems(this, shop, plr);
            case "withdrawItems" -> shopItem.withdrawItems(this, shop, plr);
            case "sellable" -> {
                shopItem.sellable = !shopItem.sellable;
                PlayerShopManager.save(shop);
                update();
                msg.msg(plr, "item.sellable." + (shopItem.sellable ? "enable" : "disable"), "item", ItemUtils.getName(shopItem.item));
            }
            case "sellPrice" -> shopItem.changeSellPrice(this, shop, plr);
            case "depositWrld" -> shopItem.depositWrld(this, shop, plr);
            case "withdrawWrld" -> shopItem.withdrawWrld(this, shop, plr);
        }
    }
}
