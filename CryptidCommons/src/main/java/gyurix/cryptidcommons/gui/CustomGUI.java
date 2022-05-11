package gyurix.cryptidcommons.gui;

import gyurix.cryptidcommons.util.StrUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class CustomGUI implements InventoryHolder {
    protected final int[] dataSlots = new int[]{
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43,
            46, 47, 48, 49, 50, 51, 52
    };
    public GUIConfig config;
    public Inventory inv;
    public Player plr;

    public CustomGUI(Player plr, GUIConfig config) {
        this.plr = plr;
        this.config = config;
    }

    public ItemStack getCustomItem(String name) {
        return config.getCustomItems().get(name);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inv;
    }

    public abstract void onClick(int slot, boolean right, boolean shift);

    public void onClose() {
    }

    public void open(Object... titleVars) {
        inv = Bukkit.createInventory(this, config.getLayout().size(), StrUtils.fillVariables(config.getTitle(), titleVars));
        config.getStaticMap().forEach((slot, is) -> inv.setItem(slot, is));
        update();
        plr.openInventory(inv);
    }

    public void update() {
        config.getCustomMap().forEach((slot, name) -> inv.setItem(slot, getCustomItem(name)));
    }
}
