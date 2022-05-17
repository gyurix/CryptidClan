package gyurix.cryptidcommons.gui;

import gyurix.cryptidcommons.util.ChatDataReader;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GUIListener implements Listener {
    public static final GUIListener instance = new GUIListener();
    public static ConcurrentHashMap<UUID, ChatDataReader> chatDataReaders = new ConcurrentHashMap<>();
    public static Plugin registeredUnder;

    public static void register(Plugin plugin) {
        if (registeredUnder != null && registeredUnder.isEnabled())
            return;
        registeredUnder = plugin;
        Bukkit.getPluginManager().registerEvents(instance, registeredUnder);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent e) {
        Player plr = e.getPlayer();
        ChatDataReader dataReader = chatDataReaders.remove(plr.getUniqueId());
        if (dataReader != null) {
            e.setCancelled(true);
            dataReader.onMessage(plr, e.getMessage());
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Inventory inv = e.getClickedInventory();
        Inventory top = e.getView().getTopInventory();
        if (top.getHolder() instanceof CustomGUI) {
            e.setCancelled(true);
            if (inv == top)
                ((CustomGUI) top.getHolder()).onClick(e.getSlot(), e.isRightClick(), e.isShiftClick());
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Inventory top = e.getView().getTopInventory();
        if (top.getHolder() instanceof CustomGUI)
            ((CustomGUI) top.getHolder()).onClose();
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        Inventory top = e.getView().getTopInventory();
        if (top.getHolder() instanceof CustomGUI)
            e.setCancelled(true);
    }
}
