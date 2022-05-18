package gyurix.villas;

import gyurix.cryptidcommons.gui.GUIListener;
import gyurix.villas.cmd.CommandVilla;
import gyurix.villas.cmd.CommandVillas;
import gyurix.villas.cmd.CommandVillasA;
import gyurix.villas.conf.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class VillasPlugin extends JavaPlugin {
    public static VillasPlugin pl;

    @Override
    public void onDisable() {

    }

    @Override
    public void onEnable() {
        pl = this;
        ConfigManager.reload();
        Bukkit.getPluginManager().registerEvents(new VillasListener(), this);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(pl, new MoveDetector(), 5, 5);
        GUIListener.register(this);

        VillaManager.loadVillas();
        new CommandVilla();
        new CommandVillas();
        new CommandVillasA();
    }
}
