package gyurix.playershops.gui;

import com.nftworlds.wallet.objects.NFTPlayer;
import com.nftworlds.wallet.objects.Network;
import com.nftworlds.wallet.objects.Wallet;
import gyurix.cryptidcommons.gui.CustomGUI;
import gyurix.cryptidcommons.util.ChatDataReader;
import gyurix.cryptidcommons.util.ItemUtils;
import gyurix.playershops.PlayerShopManager;
import gyurix.playershops.data.PlayerShop;
import gyurix.playershops.data.ShopItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static gyurix.playershops.conf.ConfigManager.conf;
import static gyurix.playershops.conf.ConfigManager.msg;

public class ShopGUI extends CustomGUI {
    private final ShopItem category;
    private ItemStack icon;
    private final boolean manage;
    private final PlayerShop shop;

    public ShopGUI(Player plr, PlayerShop shop, boolean manage, ShopItem category) {
        super(plr, conf.guis.get(category.categoryName.isEmpty() ? "shop" : "category"));
        this.category = category;
        this.manage = manage;
        this.shop = shop;
        open("player", shop.getOwnerName());
    }

    private void createCategory(int slot) {
        if (!category.categoryName.isEmpty()) {
            msg.msg(plr, "category.nosub");
            return;
        }
        int categoryLimit = conf.getCategoryLimit(plr);
        if (shop.getShopItem().countCategories() >= categoryLimit) {
            msg.msg(plr, "category.limit", "limit", categoryLimit);
            return;
        }
        if (icon == null) {
            msg.msg(plr, "category.nosel");
            return;
        }
        plr.closeInventory();
        msg.msg(plr, "category.enter");
        new ChatDataReader(plr, (categoryName) -> {
            msg.msg(plr, "category.create", "category", categoryName);
            category.subItems.put(slot, new ShopItem(icon, categoryName));
            PlayerShopManager.save(shop);
            update();
        }, () -> {
            msg.msg(plr, "category.cancel");
            plr.openInventory(inv);
        });
    }

    private void createItem(int slot) {
        int itemLimit = conf.getItemLimit(plr);
        if (shop.getShopItem().countCategories() >= itemLimit) {
            msg.msg(plr, "item.limit", "limit", itemLimit);
            return;
        }
        if (icon == null) {
            msg.msg(plr, "item.nosel");
            return;
        }
        String license = conf.categoryLicenses.get(icon.getType());
        if (license == null) {
            msg.msg(plr, "license.notlistable");
            return;
        }
        if (!shop.getLicenses().contains(license)) {
            msg.msg(plr, "license.required", "license", license);
            return;
        }
        msg.msg(plr, "item.create", "item", ItemUtils.getName(icon));
        category.subItems.put(slot, new ShopItem(icon));
        PlayerShopManager.save(shop);
        update();
    }

    @Override
    public ItemStack getCustomItem(String name) {
        switch (name) {
            case "claim" -> {
                int unclaimed = shop.countUnclaimedItems();
                return config.getCustomItem(unclaimed == 0 ? "claimNo" : "claim", "amount", unclaimed);
            }
            case "player" -> {
                return ItemUtils.makeSkull(config.getCustomItem("player"), shop.getOwnerName(), "player", shop.getOwnerName());
            }
            case "category" -> {
                return ItemUtils.fillVariables(category.item, config.getCustomItem("category", "category", category.categoryName));
            }
        }
        throw new RuntimeException("Unexpected custom item " + name);
    }

    private void handleAdminClick(int slot, ShopItem shopItem, boolean right, boolean shift) {
        if (shopItem == null) {
            if (right) {
                createCategory(slot);
                return;
            }
            createItem(slot);
            return;
        }
        if (shopItem.categoryName != null) {
            if (right && shift) {
                category.subItems.remove(slot);
                inv.setItem(slot, config.getStaticItem("glass"));
                shopItem.removeFrom(shop);
                msg.msg(plr, "category.remove", "category", shopItem.categoryName);
                return;
            }
            new ShopGUI(plr, shop, manage, shopItem);
            return;
        }

        shopItemManage(shopItem, right, shift);
    }

    @Override
    public void onBottomClick(int slot, boolean rightClick, boolean shiftClick) {
        if (!manage)
            return;
        ItemStack is = plr.getInventory().getItem(slot);
        if (is == null)
            return;
        icon = is.clone();
        msg.msg(plr, "iconsel", "icon", ItemUtils.getName(icon));
    }

    @Override
    public void onClick(int slot, boolean right, boolean shift) {
        if (slot >= inv.getSize() || slot < 0)
            return;
        String type = config.getLayout().get(slot);
        switch (type) {
            case "back" -> {
                if (!category.categoryName.isEmpty())
                    new ShopGUI(plr, shop, manage, shop.getShopItem());
            }
            case "claim" -> {
                if (!category.categoryName.isEmpty() || !manage)
                    return;
                shop.claimItems(plr);
            }
            case "exit" -> plr.closeInventory();

            default -> {
                ShopItem shopItem = category.subItems.get(slot);
                if (manage) {
                    handleAdminClick(slot, shopItem, right, shift);
                    return;
                }
                if (shopItem == null)
                    return;
                shopItemUse(shopItem, right, shift);
            }
        }
    }

    private void shopItemManage(ShopItem shopItem, boolean right, boolean shift) {
        if (shift) {
            if (right) {
                double wrld = shopItem.removeFrom(shop);
                if (wrld > 0) {
                    Wallet wallet = NFTPlayer.getByUUID(plr.getUniqueId()).getPrimaryWallet();
                    wallet.payWRLD(wrld, Network.POLYGON, "Remaining funds after deleting player shop item " + ItemUtils.getName(shopItem.item));
                }
                return;
            }
            new ManageGUI(plr, shop, shopItem, category);
            return;
        }
        if (right) {
            shopItem.sellable = !shopItem.sellable;
            update();
            msg.msg(plr, "item.sellable." + (shopItem.sellable ? "enable" : "disable"), "item", ItemUtils.getName(shopItem.item));
            return;
        }
        shopItem.buyable = !shopItem.buyable;
        update();
        msg.msg(plr, "item.buyable." + (shopItem.buyable ? "enable" : "disable"), "item", ItemUtils.getName(shopItem.item));
    }

    private void shopItemUse(ShopItem shopItem, boolean right, boolean shift) {
        if (shift) {
            if (right) {
                shopItem.sellMore(this, shop, plr);
                return;
            }
            shopItem.buyMore(this, shop, plr);
            return;
        }
        if (right) {
            shopItem.sell1(this, shop, plr);
            return;
        }
        shopItem.buy1(this, shop, plr);
    }

    @Override
    public void update() {
        category.subItems.forEach((slot, item) -> inv.setItem(slot, item.getDisplayItem(manage)));
        super.update();
    }
}
