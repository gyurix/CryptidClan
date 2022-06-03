package gyurix.bountysystem;

import gyurix.bountysystem.conf.ConfigManager;
import gyurix.bountysystem.data.BountyPlayer;
import gyurix.cryptidcommons.gui.GUIListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static gyurix.bountysystem.conf.ConfigManager.conf;

public class BountySystemPlugin extends JavaPlugin implements Listener {
    public static HashMap<UUID, Map.Entry<UUID, Long>> lastDamager = new HashMap<>();
    public static BountySystemPlugin pl;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player plr))
            return;
        Player dmgr = null;
        if (e.getDamager() instanceof Player)
            dmgr = (Player) e.getDamager();
        else if (e.getDamager() instanceof Projectile dmgrProj) {
            if (dmgrProj.getShooter() instanceof Player)
                dmgr = (Player) dmgrProj.getShooter();
        }
        if (dmgr == null)
            return;
        long expire = System.currentTimeMillis() + conf.damageExpireMs;
        lastDamager.put(plr.getUniqueId(), new AbstractMap.SimpleEntry<>(dmgr.getUniqueId(), expire));
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onEnable() {
        pl = this;
        ConfigManager.reload();
        GUIListener.register(this);
        Bukkit.getPluginManager().registerEvents(this, this);

        BountyManager.initTable();
        getCommand("bounty").setExecutor(new CommandBounty());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKill(PlayerDeathEvent e) {
        Player plr = e.getEntity();
        Map.Entry<UUID, Long> entry = lastDamager.remove(plr.getUniqueId());
        UUID killer = null;
        if (entry != null && entry.getValue() >= System.currentTimeMillis()) {
            killer = entry.getKey();
        }

        UUID finalKiller = killer;
        BountyManager.withBountyPlayer(plr.getUniqueId(), bp -> bp.incDeaths(finalKiller));
        if (finalKiller != null)
            BountyManager.withBountyPlayer(entry.getKey(), BountyPlayer::incKills);
        Bukkit.getScheduler().scheduleSyncDelayedTask(pl, () -> plr.spigot().respawn(), 1);
    }
}
