package gyurix.villas;

import gyurix.cryptidcommons.gui.GUIListener;
import gyurix.villas.cmd.CommandVillas;
import gyurix.villas.conf.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class VillasPlugin extends JavaPlugin {
    public static VillasPlugin pl;

    @Override
    public void onEnable() {
        pl = this;
        ConfigManager.reload();
        Bukkit.getPluginManager().registerEvents(new VillasListener(), this);
        Bukkit.getPluginManager().registerEvents(new GUIListener(), this);
        new CommandVillas();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(pl, new MoveDetector(), 5, 5);
    }

    @Override
    public void onDisable() {

    }
}
