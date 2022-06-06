package gyurix.timedopportunities;

import gyurix.cryptidcommons.data.AnnounceMessage;
import gyurix.cryptidcommons.data.BlockLoc;
import gyurix.cryptidcommons.util.HoloUtils;
import gyurix.cryptidcommons.util.StrUtils;
import gyurix.timedopportunities.gui.LootGUI;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static gyurix.cryptidcommons.util.StrUtils.rand;
import static gyurix.timedopportunities.TimedOpportunitiesPlugin.pl;
import static gyurix.timedopportunities.conf.ConfigManager.conf;

public class LandingManager {
    private static final ScheduledExecutorService sch = Executors.newSingleThreadScheduledExecutor();
    public static BlockLoc currentLoc;
    public static LootGUI gui;
    public static ArmorStand holo;
    public static int lightningRID;
    public static long nextLanding;

    private static long getNextLandingTime() {
        long time = System.currentTimeMillis() + conf.announceMessages.lastKey();
        long from = time - time % 86400000;
        for (int i = 0; i < 10; ++i) {
            for (String spawnTime : conf.spawningTimes) {
                long t = from + StrUtils.toTime(spawnTime);
                if (t > time) {
                    return t;
                }
            }
            time += 86400000;
        }
        return -1;
    }

    public static void launch() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(pl, () -> {
            currentLoc.toBlock().setType(Material.CHEST);
            Location lightingLoc = currentLoc.toBlock().getLocation().add(0.5, 1, 0.5);
            World w = lightingLoc.getWorld();
            w.strikeLightningEffect(lightingLoc);
            HoloUtils.spawn(currentLoc.toLocation().add(0, 1.5, 0), conf.holoLines);
            lightningRID = Bukkit.getScheduler().scheduleSyncRepeatingTask(pl, () -> {
                for (int i = 0; i < 10; ++i)
                    w.spawnParticle(Particle.ASH, lightingLoc.clone().add(rand.nextDouble() - 0.5, rand.nextDouble() * 0.75, rand.nextDouble() - 0.5), 25);
            }, 5, 5);
            gui = new LootGUI();
        });
    }

    public static void scheduleNextLanding(boolean now) {
        if (conf.locations.isEmpty())
            return;
        stop();
        scheduleNextLanding(now ? System.currentTimeMillis() + 5500L : getNextLandingTime());
    }

    private static void scheduleNextLanding(long t) {
        long time = System.currentTimeMillis();
        nextLanding = t;
        sch.schedule(LandingManager::launch, t - time, TimeUnit.MILLISECONDS);
        for (Map.Entry<Integer, AnnounceMessage> e : conf.announceMessages.entrySet()) {
            if (e.getKey() < 0)
                continue;
            AnnounceMessage msg = e.getValue();
            long delay = t - time - e.getKey() * 1000L;
            if (delay > 0)
                sch.schedule(() -> msg.broadcast("loc", currentLoc), delay, TimeUnit.MILLISECONDS);
        }
        long delay = t - time - (conf.getAnnounceMessages().lastKey() + 1) * 1000L;
        if (delay > 0)
            sch.schedule(LandingManager::setLoc, delay, TimeUnit.MILLISECONDS);
        else
            setLoc();
    }

    private static void setLoc() {
        currentLoc = conf.locations.get(rand.nextInt(conf.locations.size()));
    }

    public static void stop() {
        if (currentLoc != null) {
            currentLoc.toBlock().setType(Material.AIR);
            HoloUtils.despawn(currentLoc.toLocation().add(0, 1.5, 0), conf.holoLines.size());
        }
        Bukkit.getScheduler().cancelTask(lightningRID);
        currentLoc = null;
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.getOpenInventory().getTopInventory();
            if (p.getOpenInventory().getTopInventory().getHolder() == gui) {
                p.closeInventory();
            }
        }
    }
}
