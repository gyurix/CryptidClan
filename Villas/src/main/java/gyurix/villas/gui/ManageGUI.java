package gyurix.villas.gui;

import gyurix.cryptidcommons.data.Loc;
import gyurix.cryptidcommons.gui.CustomGUI;
import gyurix.cryptidcommons.util.ChatDataReader;
import gyurix.cryptidcommons.util.ItemUtils;
import gyurix.cryptidcommons.util.StrUtils;
import gyurix.villas.data.Group;
import gyurix.villas.data.Villa;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static gyurix.cryptidcommons.util.StrUtils.DF;
import static gyurix.villas.VillaManager.saveVilla;
import static gyurix.villas.conf.ConfigManager.conf;
import static gyurix.villas.conf.ConfigManager.msg;

public class ManageGUI extends CustomGUI {
    private final Villa villa;

    public ManageGUI(Player plr, Villa villa) {
        super(plr, conf.getGuis().get("manage"));
        this.villa = villa;
        open("villa", villa.getName());
    }

    @Override
    public ItemStack getCustomItem(String name) {
        switch (name) {
            case "price":
                return ItemUtils.fillVariables(config.getCustomItems().get("price"),
                        "price", DF.format(villa.getPrice()));
            case "buyable":
                return ItemUtils.fillVariables(config.getCustomItems().get(villa.isBuyable() ? "buyableEnabled" : "buyable"));
            case "icon":
                ItemStack icon = config.getCustomItems().get("icon");
                ItemStack out = ItemUtils.stringToItemStack(villa.getIcon());
                ItemMeta meta = out.getItemMeta();
                meta.setDisplayName(icon.getItemMeta().getDisplayName());
                meta.setLore(icon.getLore());
                out.setItemMeta(meta);
                return out;
        }
        throw new RuntimeException("Unknown custom item " + name);
    }

    @Override
    public void onClick(int slot, boolean right, boolean shift) {
        if (slot >= inv.getSize() || slot < 0)
            return;
        String type = config.getLayout().get(slot);
        switch (type) {
            case "back" -> {
                new VillasGUI(plr);
            }
            case "buyable" -> {
                if (!villa.hasPermission(plr, Group::isManage)) {
                    msg.msg(plr, "noperm.manage");
                    return;
                }
                villa.setBuyable(!villa.isBuyable());
                saveVilla(villa);
                msg.msg(plr, villa.isBuyable() ? "buyable.enable" : "buyable.disable", "villa", villa.getName());
                update();
            }
            case "icon" -> {
                if (!villa.hasPermission(plr, Group::isManage)) {
                    msg.msg(plr, "noperm.manage");
                    return;
                }
                msg.msg(plr, "iconClick");
            }
            case "price" -> {
                if (!villa.hasPermission(plr, Group::isManage)) {
                    msg.msg(plr, "noperm.manage");
                    return;
                }
                msg.msg(plr, "price.enter", "villa", villa.getName());
                plr.closeInventory();
                new ChatDataReader(plr, (priceStr) -> {
                    double price = -1;
                    try {
                        price = Double.parseDouble(priceStr);
                    } catch (NumberFormatException ignored) {
                    }
                    if (price < 0 || !Double.isFinite(price)) {
                        msg.msg(plr, "wrong.price");
                        return;
                    }
                    villa.setPrice(price);
                    new ManageGUI(plr,villa);
                    saveVilla(villa);
                    msg.msg(plr, "price.done", "villa", villa.getName(), "price", DF.format(price));
                }, () -> msg.msg(plr, "price.cancel", "villa", villa.getName()));
            }
            case "exit" -> plr.closeInventory();
            case "tp" -> {
                if (!villa.hasPermission(plr, Group::isTp)) {
                    msg.msg(plr, "noperm.tp");
                    return;
                }

                plr.teleport(villa.getSpawn().toLocation());
            }
            case "players" -> {
                new PlayersGUI(plr, villa);
            }
            case "groups" -> {
                new GroupsGUI(plr, villa);
            }
            case "setspawn" -> {
                if (!villa.hasPermission(plr, Group::isManage)) {
                    msg.msg(plr, "noperm.manage");
                    return;
                }
                Location spawn = plr.getLocation();
                if (!villa.getArea().contains(spawn)) {
                    msg.msg(plr, "wrong.spawn", "villa", villa.getName());
                    return;
                }
                villa.setSpawn(new Loc(spawn));
                saveVilla(villa);
                msg.msg(plr, "setspawn", "villa", villa.getName(), "loc", villa.getSpawn());
            }
        }
    }

    @Override
    public void onBottomClick(int slot, boolean rightClick, boolean shiftClick) {
        ItemStack is = plr.getInventory().getItem(slot);
        if (is != null) {
            if (!villa.hasPermission(plr, Group::isManage)) {
                msg.msg(plr, "noperm.manage");
                return;
            }
            is = is.clone();
            ItemMeta meta = is.getItemMeta();
            meta.setDisplayName(null);
            meta.setLore(null);
            is.setItemMeta(meta);
            villa.setIcon(ItemUtils.itemToString(is));
            msg.msg(plr, "icon", "villa", villa.getName(), "icon", StrUtils.toCamelCase(is.getType().name()));
            update();
            saveVilla(villa);
        }
    }
}
