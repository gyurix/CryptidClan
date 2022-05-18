package gyurix.villas.gui;

import gyurix.cryptidcommons.data.Loc;
import gyurix.cryptidcommons.gui.CustomGUI;
import gyurix.villas.VillaManager;
import gyurix.villas.data.Group;
import gyurix.villas.data.Villa;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

                plr.teleport(villa.getSpawn().toLocation());
                return;
            }
            case "players" -> {
                new PlayersGUI(plr, villa);
                return;
            }
            case "groups" -> {
                new GroupsGUI(plr, villa);
                return;
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
                VillaManager.saveVilla(villa);
                msg.msg(plr, "setspawn", "villa", villa.getName(), "loc", villa.getSpawn());
                return;
            }
        }
        update();
    }
}
