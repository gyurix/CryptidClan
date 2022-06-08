package gyurix.cryptidgear;

import gyurix.cryptidcommons.gui.GUIListener;
import gyurix.cryptidgear.conf.ConfigManager;
import gyurix.cryptidgear.data.PlayerData;
import gyurix.cryptidgear.data.Weapon;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class CryptidGearPlugin extends JavaPlugin implements Listener {
    public static CryptidGearPlugin pl;

    @EventHandler
    public void onAbilityUse(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        Player plr = e.getPlayer();
        ItemStack is = e.getItem();
        Weapon weapon = GearManager.getWeapon(is);
        if (weapon != null)
            weapon.useAbility(plr);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @SneakyThrows
    public void onDamage(EntityDamageByEntityEvent e) {
        Player dmgr = null;
        if (e.getDamager() instanceof Player)
            dmgr = (Player) e.getDamager();
        else if (e.getDamager() instanceof Projectile dmgrProj) {
            if (dmgrProj.getShooter() instanceof Player)
                dmgr = (Player) dmgrProj.getShooter();
        }
        if (dmgr == null)
            return;
        PlayerData pd = GearManager.playerData.get(dmgr.getUniqueId());
        if (pd.getStrikeUntil() > System.currentTimeMillis()) {
            e.setDamage(e.getDamage(EntityDamageEvent.DamageModifier.BASE) * pd.getStrike().getDamage());
        }
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
        getCommand("gear").setExecutor(new CommandGear());
    }
}
