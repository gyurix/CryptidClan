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

import static gyurix.cryptidcommons.util.StrUtils.DF;

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

        @Override
        public String toString(Ability settings) {
            return "Aura (radius = " + DF.format(settings.getRadius()) + ", damage = " + DF.format(settings.getDamage()) + ")";
        }
    },
    EXPLOSION {
        @Override
        public void activate(Player plr, Ability settings) {
            plr.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5, 9));
            plr.getLocation().createExplosion(plr, (float) settings.getDamage(), false, false);
        }

        @Override
        public String toString(Ability settings) {
            return "Explosion (damage = " + DF.format(settings.getDamage()) + ")";
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

        @Override
        public String toString(Ability settings) {
            return "Flame (radius = " + DF.format(settings.getRadius()) + ", duration = " + settings.getDurationSeconds() + "s)";
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

        @Override
        public String toString(Ability settings) {
            return "Launch (radius = " + DF.format(settings.getRadius()) + ", intensity = " + settings.getDamage() + ")";
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

        @Override
        public String toString(Ability settings) {
            return "Lightning (radius = " + DF.format(settings.getRadius()) + ", level = " + settings.getLevel() + ")";
        }
    },
    POTION {
        @Override
        public void activate(Player plr, Ability settings) {
            plr.addPotionEffect(new PotionEffect(Objects.requireNonNull(PotionEffectType.getByName(settings.getEffectType())), settings.getDurationSeconds() * 20, settings.getLevel() - 1));
        }

        @Override
        public String toString(Ability settings) {
            return "Potion (" + settings.getEffectType() + " " + settings.getLevel() + " - " + settings.getDurationSeconds() + "s)";
        }
    },
    SLICE {
        @Override
        @SneakyThrows
        public void activate(Player plr, Ability settings) {
            PlayerData pd = GearManager.playerData.get(plr.getUniqueId());
            pd.setStrikeUntil(System.currentTimeMillis() + settings.getDurationSeconds() * 1000L);
        }

        @Override
        public String toString(Ability settings) {
            return "Slice (" + settings.getEffectType() + " " + settings.getLevel() + " - " + settings.getDurationSeconds() + "s)";
        }
    };

    public abstract void activate(Player plr, Ability settings);

    public abstract String toString(Ability settings);
}
