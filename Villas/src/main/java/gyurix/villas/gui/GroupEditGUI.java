package gyurix.villas.gui;

import gyurix.cryptidcommons.gui.CustomGUI;
import gyurix.cryptidcommons.util.ItemUtils;
import gyurix.cryptidcommons.util.StrUtils;
import gyurix.villas.VillaManager;
import gyurix.villas.data.Group;
import gyurix.villas.data.Villa;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static gyurix.villas.conf.ConfigManager.conf;
import static gyurix.villas.conf.ConfigManager.msg;

public class GroupEditGUI extends CustomGUI {
    private final Group group;
    private final Villa villa;

    public GroupEditGUI(Player plr, Villa villa, Group group) {
        super(plr, conf.getGuis().get("groupEdit"));
        this.villa = villa;
        this.group = group;
        open("villa", villa.getName(), "group", group.getName());
    }

    @Override
    public ItemStack getCustomItem(String name) {
        if (name.equals("icon")) {
            ItemStack icon = ItemUtils.fillVariables(config.getCustomItems().get("icon"), "group", group.getName());
            icon.setType(group.getIcon());
            return icon;
        }
        boolean enabled = group.isFlagEnabled(name);
        return config.getCustomItems().get(name + (enabled ? "Enabled" : ""));
    }

    @Override
    public void onBottomClick(int slot, boolean rightClick, boolean shiftClick) {
        ItemStack is = plr.getInventory().getItem(slot);
        if (is != null) {
            group.setIcon(is.getType());
            msg.msg(plr, "icon", "group", group.getName(), "icon", StrUtils.toCamelCase(group.getIcon().name()));
            update();
        }
    }

    @Override
    public void onClick(int slot, boolean right, boolean shift) {
        if (slot >= inv.getSize() || slot < 0)
            return;
        String type = slot < config.getLayout().size() ? config.getLayout().get(slot) : "";
        switch (type) {
            case "exit" -> {
                plr.closeInventory();
                return;
            }
            case "back" -> {
                new GroupsGUI(plr, villa);
                return;
            }
            case "glass" -> {
                return;
            }
        }
        if (group.getName().equals("owner")) {
            msg.msg(plr, "group.readonly", "group", group.getName());
            return;
        }
        if (villa.getGroups().get(group.getName()) != group) {
            new GroupsGUI(plr, villa);
            return;
        }
        if (!villa.hasPermission(plr, Group::isManage)) {
            msg.msg(plr, "noperm.manage");
            return;
        }

        switch (type) {
            case "icon" -> {
                msg.msg(plr, "group.iconClick");
                return;
            }
            case "remove" -> {
                villa.removeGroup(plr, group.getName());
                return;
            }
            case "build" -> {
                group.setBuild(!group.isBuild());
                msg.msg(plr, "group." + (group.isBuild() ? "enable" : "disable"), "permission", "build", "group", group.getName());
            }
            case "enter" -> {
                group.setEnter(!group.isEnter());
                msg.msg(plr, "group." + (group.isEnter() ? "enable" : "disable"), "permission", "enter", "group", group.getName());
            }
            case "info" -> {
                group.setInfo(!group.isInfo());
                msg.msg(plr, "group." + (group.isInfo() ? "enable" : "disable"), "permission", "info", "group", group.getName());
            }
            case "manage" -> {
                group.setManage(!group.isManage());
                msg.msg(plr, "group." + (group.isManage() ? "enable" : "disable"), "permission", "manage", "group", group.getName());
            }
            case "see" -> {
                group.setSee(!group.isSee());
                msg.msg(plr, "group." + (group.isSee() ? "enable" : "disable"), "permission", "see", "group", group.getName());
            }
            case "tp" -> {
                group.setTp(!group.isTp());
                msg.msg(plr, "group." + (group.isTp() ? "enable" : "disable"), "permission", "tp", "group", group.getName());
            }
            case "use" -> {
                group.setUse(!group.isUse());
                msg.msg(plr, "group." + (group.isUse() ? "enable" : "disable"), "permission", "use", "group", group.getName());
            }
        }
        update();
    }

    @Override
    public void onClose() {
        VillaManager.saveVilla(villa);
    }
}
