package gyurix.timedopportunities;

import gyurix.cryptidcommons.data.BlockLoc;
import gyurix.cryptidcommons.gui.GUIListener;
import gyurix.timedopportunities.conf.ConfigManager;
import gyurix.timedopportunities.gui.LootGUI;
import org.bukkit.Bukkit;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;

public class TimedOpportunitiesPlugin extends JavaPlugin implements Listener {
    public static TimedOpportunitiesPlugin pl;

    @EventHandler
    public void onChestOpen(InventoryOpenEvent e) {
        if (LandingManager.currentLoc == null)
            return;
        Player plr = (Player) e.getPlayer();
        Inventory inv = e.getInventory();
        InventoryHolder holder = inv.getHolder();
        if (holder instanceof Chest chest) {
            if (LandingManager.currentLoc.equals(new BlockLoc(chest.getBlock()))) {
                e.setCancelled(true);
                Bukkit.getScheduler().scheduleSyncDelayedTask(pl, () -> {
                    plr.openInventory(LandingManager.gui.inv);
                });
            }
        }
    }

    @Override
    public void onDisable() {
        LandingManager.stop();
    }

    @Override
    public void onEnable() {
        pl = this;
        ConfigManager.reload();
        GUIListener.register(this);
        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("topp").setExecutor(new CommandTopp());
        LandingManager.scheduleNextLanding(false);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Inventory inv = e.getClickedInventory();
        Inventory top = e.getView().getTopInventory();
        if (top.getHolder() instanceof LootGUI) {
            e.setCancelled(true);
            if (inv == top)
                ((LootGUI) top.getHolder()).onClick((Player) e.getWhoClicked(), e.getSlot());
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        Inventory top = e.getView().getTopInventory();
        if (top.getHolder() instanceof LootGUI)
            e.setCancelled(true);
    }
}
