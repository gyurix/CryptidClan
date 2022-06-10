package gyurix.cryptidgear.data;

import gyurix.cryptidgear.GearManager;
import lombok.SneakyThrows;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public enum AbilityType {
    AURA {
        @Override
        public void activate(Player plr, Ability settings) {
            Location loc = plr.getLocation();
            for (LivingEntity e : plr.getWorld().getLivingEntities()) {
                if (e instanceof ArmorStand || e == plr)
                    continue;
                double dist = e.getLocation().distance(loc);
                if (dist <= settings.getRadius()) {
                    e.damage(settings.getDamage(), plr);
                    e.getWorld().spawnParticle(Particle.REDSTONE, e.getEyeLocation(), 3);
                }
            }
        }
    },
    EXPLOSION {
        @Override
        public void activate(Player plr, Ability settings) {
            plr.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5, 9));
            plr.getLocation().createExplosion(plr, (float) settings.getDamage(), false, false);
        }
    },
    FLAME {
        @Override
        public void activate(Player plr, Ability settings) {
            Location loc = plr.getLocation();
            for (LivingEntity e : plr.getWorld().getLivingEntities()) {
                if (e instanceof ArmorStand || e == plr)
                    continue;
                double dist = e.getLocation().distance(loc);
                if (dist <= settings.getRadius()) {
                    e.setFireTicks(settings.getDurationSeconds() * 20);
                }
            }
        }
    },
    LAUNCH {
        @Override
        public void activate(Player plr, Ability settings) {
            Location loc = plr.getLocation();
            for (LivingEntity e : plr.getWorld().getLivingEntities()) {
                if (e instanceof ArmorStand || e == plr)
                    continue;
                double dist = e.getLocation().distance(loc);
                if (dist <= settings.getRadius()) {
                    e.setVelocity(e.getLocation().subtract(plr.getLocation()).toVector().normalize().multiply(settings.getDamage()));
                }
            }
        }
    },
    LIGHTNING {
        @Override
        public void activate(Player plr, Ability settings) {
            Location loc = plr.getLocation();
            for (LivingEntity e : plr.getWorld().getLivingEntities()) {
                if (e instanceof ArmorStand || e == plr)
                    continue;
                double dist = e.getLocation().distance(loc);
                if (dist <= settings.getRadius()) {
                    for (int i = 0; i < settings.getLevel(); ++i) {
                        loc.getWorld().strikeLightning(e.getLocation());
                    }
                }
            }
        }
    },
    POTION {
        @Override
        public void activate(Player plr, Ability settings) {
            plr.addPotionEffect(new PotionEffect(Objects.requireNonNull(PotionEffectType.getByName(settings.getEffectType())), settings.getDurationSeconds() / 20, settings.getLevel() - 1));
        }
    },
    SLICE {
        @Override
        @SneakyThrows
        public void activate(Player plr, Ability settings) {
            PlayerData pd = GearManager.playerData.get(plr.getUniqueId());
            pd.setStrikeUntil(System.currentTimeMillis() + settings.getDurationSeconds() * 1000L);
        }
    };

    public abstract void activate(Player plr, Ability settings);
}
