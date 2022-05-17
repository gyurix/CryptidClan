package gyurix.villas.gui;

import gyurix.cryptidcommons.gui.CustomGUI;
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
        open();
    }

    @Override
    public ItemStack getCustomItem(String name) {
        boolean enabled = group.isFlagEnabled(name);
        return config.getCustomItems().get(name + (enabled ? "Enabled" : ""));
    }

    @Override
    public void onClick(int slot, boolean right, boolean shift) {
        if (slot >= inv.getSize() || slot < 0)
            return;
        String type = config.getLayout().get(slot);
        switch (type) {
            case "exit" -> plr.closeInventory();
            case "back" -> new GroupsGUI(plr, villa);
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
                //msg.msg(plr, "group.iconClick");
            }
            case "remove" -> {
                if (!group.isRemovable()) {
                    msg.msg(plr, "group.notremovable");
                    return;
                }
                if (villa.getPlayers().containsValue(group.getName())) {
                    return;
                }
                msg.msg(plr, "group.remove", "group", group.getName());
            }
            case "build" -> {
                group.setBuild(!group.isBuild());
                msg.msg(plr, "group." + (group.isBuild() ? "enable" : "disable"), "permission", "build");
            }
            case "enter" -> {
                group.setEnter(!group.isEnter());
                msg.msg(plr, "group." + (group.isEnter() ? "enable" : "disable"), "permission", "enter");
            }
            case "info" -> {
                group.setInfo(!group.isInfo());
                msg.msg(plr, "group." + (group.isInfo() ? "enable" : "disable"), "permission", "info");
            }
            case "manage" -> {
                group.setManage(!group.isManage());
                msg.msg(plr, "group." + (group.isManage() ? "enable" : "disable"), "permission", "manage");
            }
            case "see" -> {
                group.setSee(!group.isSee());
                msg.msg(plr, "group." + (group.isSee() ? "enable" : "disable"), "permission", "see");
            }
            case "tp" -> {
                group.setTp(!group.isTp());
                msg.msg(plr, "group." + (group.isTp() ? "enable" : "disable"), "permission", "tp");
            }
            case "use" -> {
                group.setUse(!group.isUse());
                msg.msg(plr, "group." + (group.isUse() ? "enable" : "disable"), "permission", "use");
            }
        }
        update();
    }

    @Override
    public void onClose() {
        VillaManager.saveVilla(villa);
    }
}
