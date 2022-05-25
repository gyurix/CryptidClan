package gyurix.playershops;

import gyurix.cryptidcommons.gui.GUIListener;
import gyurix.playershops.cmd.CommandPlayerShop;
import gyurix.playershops.cmd.CommandPlayerShopA;
import gyurix.playershops.conf.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerShopsPlugin extends JavaPlugin {
    public static PlayerShopsPlugin pl;

    @Override
    public void onDisable() {

    }

    @Override
    public void onEnable() {
        pl = this;
        ConfigManager.reload();
        GUIListener.register(this);

        PlayerShopManager.initTable();
        new CommandPlayerShop();
        new CommandPlayerShopA();
    }
}
