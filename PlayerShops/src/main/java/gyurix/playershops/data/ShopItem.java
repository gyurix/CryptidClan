package gyurix.playershops.data;

import com.nftworlds.wallet.objects.NFTPlayer;
import com.nftworlds.wallet.objects.Network;
import com.nftworlds.wallet.objects.Wallet;
import gyurix.cryptidcommons.data.WRLDRunnable;
import gyurix.cryptidcommons.gui.CustomGUI;
import gyurix.cryptidcommons.util.ChatDataReader;
import gyurix.cryptidcommons.util.ItemUtils;
import gyurix.playershops.PlayerShopManager;
import gyurix.playershops.gui.ShopGUI;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

import static gyurix.cryptidcommons.util.StrUtils.DF;
import static gyurix.playershops.conf.ConfigManager.conf;
import static gyurix.playershops.conf.ConfigManager.msg;

@NoArgsConstructor
public class ShopItem {
    public double accumulatedWRLD;
    public double buyPrice;
    public boolean buyable;
    public String categoryName = "";
    public ItemStack item;
    public double sellPrice;
    public boolean sellable;
    public int stock;
    public HashMap<Integer, ShopItem> subItems = new HashMap<>();

    public ShopItem(ItemStack icon, String categoryName) {
        this.item = icon.clone();
        this.categoryName = categoryName;
    }

    public ShopItem(ItemStack icon) {
        this.item = icon.clone();
        this.categoryName = null;
    }

    public void buy1(ShopGUI gui, PlayerShop shop, Player plr) {
        doBuy(gui, shop, plr, 1);
    }

    public void buyMore(ShopGUI gui, PlayerShop shop, Player plr) {
        doBuy(gui, shop, plr, 16);
    }

    public void changeBuyPrice(CustomGUI gui, PlayerShop shop, Player plr) {
        plr.closeInventory();
        msg.msg(plr, "item.buyPrice.enter", "item", ItemUtils.getName(item));
        new ChatDataReader(plr, (buyPrice) -> {
            try {
                double price = Double.parseDouble(buyPrice);
                if (Double.isFinite(price) && price >= 0) {
                    this.buyPrice = price;
                    PlayerShopManager.save(shop);
                    msg.msg(plr, "item.buyPrice.done", "price", DF.format(price), "item", ItemUtils.getName(item));
                    gui.update();
                    plr.openInventory(gui.getInventory());
                    return;
                }
                msg.msg(plr, "wrong.price");
            } catch (Throwable err) {
                err.printStackTrace();
                msg.msg(plr, "wrong.price");
            }
            plr.openInventory(gui.getInventory());
        }, () -> {
            msg.msg(plr, "item.buyPrice.cancel", "item", ItemUtils.getName(item));
            plr.openInventory(gui.getInventory());
        });
    }

    public void changeSellPrice(CustomGUI gui, PlayerShop shop, Player plr) {
        plr.closeInventory();
        msg.msg(plr, "item.sellPrice.enter", "item", ItemUtils.getName(item));
        new ChatDataReader(plr, (sellPrice) -> {
            try {
                double price = Double.parseDouble(sellPrice);
                if (Double.isFinite(price) && price >= 0) {
                    this.sellPrice = price;
                    PlayerShopManager.save(shop);
                    msg.msg(plr, "item.sellPrice.done", "price", DF.format(price), "item", ItemUtils.getName(item));
                    gui.update();
                    plr.openInventory(gui.getInventory());
                    return;
                }
                msg.msg(plr, "wrong.price");
            } catch (Throwable err) {
                msg.msg(plr, "wrong.price");
            }
            plr.openInventory(gui.getInventory());
        }, () -> {
            msg.msg(plr, "item.sellPrice.cancel", "item", ItemUtils.getName(item));
            plr.openInventory(gui.getInventory());
        });
    }

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

    public void depositItems(CustomGUI gui, PlayerShop shop, Player plr) {
        msg.msg(plr, "item.deposit.enter", "item", ItemUtils.getName(item));
        new ChatDataReader(plr, (am) -> {
            try {
                int amount = Integer.parseInt(am);
                if (amount <= 0) {
                    msg.msg(plr, "wrong.amount");
                    plr.openInventory(gui.getInventory());
                    return;
                }
                int has = ItemUtils.countItem(plr, item);
                if (has < amount) {
                    msg.msg(plr, "item.deposit.notenough", "item", ItemUtils.getName(item));
                    plr.openInventory(gui.getInventory());
                    return;
                }
                ItemStack is = item.clone();
                is.setAmount(amount);
                plr.getInventory().remove(is);

                PlayerShopManager.save(shop);
                msg.msg(plr, "item.deposit.done", "item", ItemUtils.getName(is));
                gui.update();
                plr.openInventory(gui.getInventory());
                return;
            } catch (Throwable err) {
                msg.msg(plr, "wrong.amount");
            }
            plr.openInventory(gui.getInventory());
        }, () -> {
            msg.msg(plr, "item.deposit.cancel", "item", ItemUtils.getName(item));
            plr.openInventory(gui.getInventory());
        });
    }

    public void depositWrld(CustomGUI gui, PlayerShop shop, Player plr) {
        msg.msg(plr, "wrld.deposit.enter", "item", ItemUtils.getName(item));
        new ChatDataReader(plr, (am) -> {
            try {
                double amount = Double.parseDouble(am);
                if (!Double.isFinite(amount) || amount <= 0) {
                    msg.msg(plr, "wrong.amount");
                    plr.openInventory(gui.getInventory());
                    return;
                }
                Wallet wallet = NFTPlayer.getByUUID(plr.getUniqueId()).getPrimaryWallet();
                wallet.requestWRLD(amount, Network.POLYGON, "PlayerShop deposit for item " + ItemUtils.getName(item), false,
                        (WRLDRunnable) () -> {
                            accumulatedWRLD += amount;
                            PlayerShopManager.save(shop);
                            msg.msg(plr, "wrld.deposit.done", "amount", DF.format(am));
                            gui.update();
                            plr.openInventory(gui.getInventory());
                        });
                return;
            } catch (Throwable err) {
                msg.msg(plr, "wrong.amount");
            }
            plr.openInventory(gui.getInventory());
        }, () -> {
            msg.msg(plr, "wrld.deposit.cancel", "item", ItemUtils.getName(item));
            plr.openInventory(gui.getInventory());
        });
    }

    private void doBuy(ShopGUI shopGUI, PlayerShop shop, Player plr, int amount) {
        if (!buyable) {
            if (plr.getOpenInventory().getTopInventory() != shopGUI.getInventory())
                plr.openInventory(shopGUI.getInventory());
            msg.msg(plr, "buy.disabled");
            return;
        }
        int has = ItemUtils.countItemSpace(plr, item);
        if (has < amount) {
            if (plr.getOpenInventory().getTopInventory() != shopGUI.getInventory())
                plr.openInventory(shopGUI.getInventory());
            msg.msg(plr, "buy.notenough");
            return;
        }
        if (stock < amount) {
            if (plr.getOpenInventory().getTopInventory() != shopGUI.getInventory())
                plr.openInventory(shopGUI.getInventory());
            msg.msg(plr, "buy.notenoughshop");
            return;
        }
        ItemStack is = item.clone();
        is.setAmount(amount);
        ItemUtils.addItem(plr.getInventory(), is);

        plr.closeInventory();
        Wallet wallet = NFTPlayer.getByUUID(plr.getUniqueId()).getPrimaryWallet();
        double lockedBuyPrice = buyPrice * amount;
        wallet.requestWRLD(lockedBuyPrice, Network.POLYGON, "PlayerShop - Buying " + ItemUtils.getName(is), false,
                (WRLDRunnable) () -> {
                    if (stock < amount) {
                        wallet.payWRLD(lockedBuyPrice, Network.POLYGON, "PlayerShop - Refund " + ItemUtils.getName(is));
                        msg.msg(plr, "buy.notenoughshop");
                        shopGUI.update();
                        plr.openInventory(shopGUI.getInventory());
                        return;
                    }
                    int hasSpace = ItemUtils.countItemSpace(plr, item);
                    if (hasSpace < amount) {
                        wallet.payWRLD(lockedBuyPrice, Network.POLYGON, "PlayerShop - Refund " + ItemUtils.getName(is));
                        msg.msg(plr, "buy.notenough");
                        shopGUI.update();
                        plr.openInventory(shopGUI.getInventory());
                        return;
                    }
                    ItemUtils.addItem(plr.getInventory(), is);
                    stock -= amount;
                    accumulatedWRLD += lockedBuyPrice * (1 - conf.getServerFeePercentage() / 100.0);
                    PlayerShopManager.save(shop);
                    msg.msg(plr, "buy.done", "item", ItemUtils.getName(is), "price", lockedBuyPrice * (1 - conf.getServerFeePercentage() / 100.0));
                    shopGUI.update();
                    plr.openInventory(shopGUI.getInventory());
                });
    }

    private void doSell(ShopGUI shopGUI, PlayerShop shop, Player plr, int amount) {
        if (!sellable) {
            if (plr.getOpenInventory().getTopInventory() != shopGUI.getInventory())
                plr.openInventory(shopGUI.getInventory());
            msg.msg(plr, "sell.disabled");
            return;
        }
        int has = ItemUtils.countItem(plr, item);
        if (has < amount) {
            if (plr.getOpenInventory().getTopInventory() != shopGUI.getInventory())
                plr.openInventory(shopGUI.getInventory());
            msg.msg(plr, "sell.notenough");
            return;
        }
        if (accumulatedWRLD < sellPrice * amount) {
            if (plr.getOpenInventory().getTopInventory() != shopGUI.getInventory())
                plr.openInventory(shopGUI.getInventory());
            msg.msg(plr, "sell.notenoughshop");
            return;
        }
        ItemStack is = item.clone();
        is.setAmount(amount);
        plr.getInventory().remove(is);

        Wallet wallet = NFTPlayer.getByUUID(plr.getUniqueId()).getPrimaryWallet();
        wallet.payWRLD(sellPrice * (1 - conf.getServerFeePercentage() / 100.0), Network.POLYGON, "PlayerShop - Selling " + ItemUtils.getName(is));
        stock += amount;
        accumulatedWRLD -= sellPrice * amount;
        PlayerShopManager.save(shop);
        msg.msg(plr, "sell.done", "item", ItemUtils.getName(is), "price", sellPrice * (1 - conf.getServerFeePercentage() / 100.0));
        shopGUI.update();
        if (plr.getOpenInventory().getTopInventory() != shopGUI.getInventory())
            plr.openInventory(shopGUI.getInventory());
    }

    public ItemStack getDisplayItem(boolean admin) {
        if (!admin && !buyable && !sellable)
            return conf.guis.get("shop").getStaticItem("glass");
        List<String> lore = admin ? conf.editLore : buyable ? sellable ? conf.buySellLore : conf.buyLore : conf.sellLore;
        return ItemUtils.addLore(item.clone(), lore,
                "allowBuy", buyable ? "§ayes" : "§cno",
                "maxBuy", stock,
                "buyPrice", DF.format(buyPrice),
                "allowSell", sellable ? "§ayes" : "§cno",
                "maxSell", DF.format((int) accumulatedWRLD / (sellPrice * (1 + conf.serverFeePercentage / 100.0))),
                "sellPrice", DF.format(sellPrice),
                "stored", DF.format(accumulatedWRLD),
                "items", stock);
    }

    public double removeFrom(PlayerShop shop) {
        if (categoryName == null) {
            shop.getOverflow().put(item, shop.getOverflow().getOrDefault(item, 0) + stock);
            return 0;
        }
        double sum = 0;
        for (ShopItem value : subItems.values()) {
            sum += value.removeFrom(shop);
        }
        return sum;
    }

    public void sell1(ShopGUI shopGUI, PlayerShop shop, Player plr) {
        doSell(shopGUI, shop, plr, 1);
    }

    public void sellMore(ShopGUI shopGUI, PlayerShop shop, Player plr) {
        doSell(shopGUI, shop, plr, 16);
    }

    public void withdrawItems(CustomGUI gui, PlayerShop shop, Player plr) {
        msg.msg(plr, "item.withdraw.enter", "item", ItemUtils.getName(item));
        new ChatDataReader(plr, (am) -> {
            try {
                int amount = Integer.parseInt(am);
                if (amount <= 0) {
                    msg.msg(plr, "wrong.amount");
                    plr.openInventory(gui.getInventory());
                    return;
                }
                if (stock < amount) {
                    msg.msg(plr, "item.withdraw.notenough", "item", ItemUtils.getName(item));
                    plr.openInventory(gui.getInventory());
                    return;
                }
                ItemStack is = item.clone();
                is.setAmount(amount);
                int left = ItemUtils.addItem(plr.getInventory(), is);
                if (left > 0)
                    shop.getOverflow().put(item, shop.getOverflow().getOrDefault(item, 0) + left);
                PlayerShopManager.save(shop);
                msg.msg(plr, "item.withdraw.done", "item", ItemUtils.getName(is));
                gui.update();
                plr.openInventory(gui.getInventory());
                return;
            } catch (Throwable err) {
                msg.msg(plr, "wrong.amount");
            }
            plr.openInventory(gui.getInventory());
        }, () -> {
            msg.msg(plr, "item.withdraw.cancel", "item", ItemUtils.getName(item));
            plr.openInventory(gui.getInventory());
        });
    }

    public void withdrawWrld(CustomGUI gui, PlayerShop shop, Player plr) {
        msg.msg(plr, "wrld.withdraw.enter", "item", ItemUtils.getName(item));
        new ChatDataReader(plr, (am) -> {
            try {
                double amount = Double.parseDouble(am);
                if (amount > accumulatedWRLD) {
                    msg.msg(plr, "wrld.withdraw.notenough", "item", ItemUtils.getName(item));
                    plr.openInventory(gui.getInventory());
                    return;
                }
                if (!Double.isFinite(amount) || amount <= 0) {
                    msg.msg(plr, "wrong.amount");
                    plr.openInventory(gui.getInventory());
                    return;
                }
                Wallet wallet = NFTPlayer.getByUUID(plr.getUniqueId()).getPrimaryWallet();
                wallet.payWRLD(amount, Network.POLYGON, "PlayerShop withdrawal from item " + ItemUtils.getName(item));
                accumulatedWRLD -= amount;
                PlayerShopManager.save(shop);
                msg.msg(plr, "wrld.withdraw.done", "amount", DF.format(am));
                gui.update();
                plr.openInventory(gui.getInventory());
                return;
            } catch (Throwable err) {
                msg.msg(plr, "wrong.amount");
            }
            plr.openInventory(gui.getInventory());
        }, () -> {
            msg.msg(plr, "wrld.withdraw.cancel", "item", ItemUtils.getName(item));
            plr.openInventory(gui.getInventory());
        });
    }
}
