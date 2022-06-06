package gyurix.timedopportunities.gui;

import gyurix.timedopportunities.LandingManager;
import gyurix.timedopportunities.data.Reward;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

import static gyurix.timedopportunities.conf.ConfigManager.conf;

public class LootGUI implements InventoryHolder {
    public HashMap<Integer, List<String>> commands = new HashMap<>();
    public Inventory inv;

    public LootGUI() {
        inv = Bukkit.createInventory(this, conf.lootGUI.getLayout().size(), conf.lootGUI.getTitle());
        for (int i = 0; i < inv.getSize(); ++i) {
            Reward reward = conf.getRandomReward();
            if (reward != null) {
                inv.setItem(i, reward.item);
                commands.put(i, reward.commands);
            } else {
                inv.setItem(i, conf.lootGUI.getStaticItem("glass"));
            }
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inv;
    }

    public void onClick(Player plr, int slot) {
        List<String> cmdList = commands.remove(slot);
        if (cmdList == null)
            return;
        inv.setItem(slot, conf.lootGUI.getStaticItem("claimed"));
        for (String cmd : cmdList) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("<player>", plr.getName()));
        }
        if (commands.isEmpty()) {
            conf.announceMessages.get(-1).broadcast();
            LandingManager.scheduleNextLanding(false);
        }
    }
}
