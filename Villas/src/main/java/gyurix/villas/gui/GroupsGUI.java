package gyurix.villas.gui;

import gyurix.cryptidcommons.gui.CustomGUI;
import gyurix.villas.data.Villa;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static gyurix.villas.conf.ConfigManager.conf;

public class GroupsGUI extends CustomGUI {
    private final Villa villa;

    public GroupsGUI(Player plr, Villa villa) {
        super(plr, conf.getGuis().get("groups"));
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
        if (type.equals("exit")) {
            plr.closeInventory();
            return;
        } else if (type.equals("back")) {
            new ManageGUI(plr, villa);
            return;
        }
        update();
    }
}
