package gyurix.villas.gui;

import gyurix.cryptidcommons.data.Loc;
import gyurix.cryptidcommons.gui.CustomGUI;
import gyurix.villas.VillaManager;
import gyurix.villas.data.Group;
import gyurix.villas.data.Villa;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static gyurix.villas.conf.ConfigManager.conf;
import static gyurix.villas.conf.ConfigManager.msg;

public class ManageGUI extends CustomGUI {
    private final Villa villa;

    public ManageGUI(Player plr, Villa villa) {
        super(plr, conf.getGuis().get("manage"));
        this.villa = villa;
        open();
    }

    @Override
    public ItemStack getCustomItem(String name) {
        return config.getCustomItems().get(name);
    }

    @Override
    public void onClick(int slot, boolean right, boolean shift) {
        if (slot >= inv.getSize() || slot < 0)
            return;
        String type = config.getLayout().get(slot);
        switch (type) {
            case "exit" -> plr.closeInventory();
            case "tp" -> {
                if (!villa.hasPermission(plr, Group::isTp)) {
                    msg.msg(plr, "noperm.tp");
                    return;
                }
            }
            case "players" -> new PlayersGUI(plr, villa);
            case "groups" -> new GroupsGUI(plr, villa);
            case "spawn" -> {
                if (!villa.hasPermission(plr, Group::isManage)) {
                    msg.msg(plr, "noperm.manage");
                    return;
                }
                villa.setSpawn(new Loc(plr.getLocation()));
                VillaManager.saveVilla(villa);
                msg.msg(plr, "setspawn", "villa", villa.getName(), "loc", villa.getSpawn());
            }
        }
        update();
    }
}
