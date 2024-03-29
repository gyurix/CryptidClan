package gyurix.cryptidgear.data;


import gyurix.cryptidcommons.util.StrUtils;
import gyurix.cryptidgear.GearManager;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;

import static gyurix.cryptidgear.conf.ConfigManager.msg;

@Getter
public class Weapon {
    private Ability ability;
    private double damage;
    private String name;

    @SneakyThrows
    public void useAbility(Player plr) {
        PlayerData pd = GearManager.playerData.get(plr.getUniqueId());
        long nextUse = pd.getNextWeaponAbilityUse().getOrDefault(name, 0L);
        long time = System.currentTimeMillis();
        if (nextUse > time) {
            msg.msg(plr, "ability.wait", "time", StrUtils.formatTime(nextUse - time));
            return;
        }
        ability.getType().activate(plr, ability);
        plr.getWorld().spawnParticle(ability.getParticle(), plr.getLocation(), 5);
        pd.getNextWeaponAbilityUse().put(name, time + ability.getCooldown() * 1000L);
        msg.msg(plr, "ability.use");
    }
}
