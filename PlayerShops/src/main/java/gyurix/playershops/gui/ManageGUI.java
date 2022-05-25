package gyurix.playershops.gui;

import gyurix.cryptidcommons.gui.CustomGUI;
import org.bukkit.entity.Player;

import static gyurix.playershops.conf.ConfigManager.conf;

public class ManageGUI extends CustomGUI {
    public ManageGUI(Player plr) {
        super(plr, conf.guis.get("manage"));
    }

    @Override
    public void onClick(int slot, boolean right, boolean shift) {
        if (slot >= inv.getSize() || slot < 0)
            return;
        String type = config.getLayout().get(slot);
    }
}
