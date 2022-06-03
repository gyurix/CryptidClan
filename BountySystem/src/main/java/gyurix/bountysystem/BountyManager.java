package gyurix.bountysystem;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import gyurix.bountysystem.data.BountyPlayer;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static gyurix.bountysystem.BountySystemPlugin.pl;
import static gyurix.bountysystem.conf.ConfigManager.conf;
import static gyurix.bountysystem.conf.ConfigManager.gson;

public class BountyManager {
    private static final LoadingCache<UUID, BountyPlayer> players = CacheBuilder
            .newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {
                @Override
                public BountyPlayer load(@NotNull UUID uuid) {
                    AtomicReference<BountyPlayer> out = new AtomicReference<>();
                    conf.getMySQL().query("SELECT `data` FROM `" + conf.getMySQL().table + "` WHERE `uuid` = ? LIMIT 1", (rs) -> {
                        if (rs.next())
                            out.set(gson.fromJson(rs.getString(1), BountyPlayer.class));
                    }, uuid);
                    if (out.get() == null)
                        out.set(new BountyPlayer(uuid));
                    return out.get();
                }
            });

    public static void initTable() {
        Bukkit.getScheduler().runTaskAsynchronously(pl,
                () -> conf.getMySQL().command("CREATE TABLE IF NOT EXISTS `" + conf.getMySQL().table + "` (`uuid` CHAR(40) UNIQUE PRIMARY KEY, `data` MEDIUMTEXT)"));

    }

    public static void save(BountyPlayer bountyPlayer) {
        Bukkit.getScheduler().runTaskAsynchronously(pl, () -> {
            int updated = conf.getMySQL().update("UPDATE `" + conf.getMySQL().table + "` SET `data` = ? WHERE `uuid` = ?", gson.toJson(bountyPlayer), bountyPlayer.getUuid());
            if (updated == 0)
                conf.getMySQL().update("INSERT INTO `" + conf.getMySQL().table + "` VALUES ( ?, ? )", bountyPlayer.getUuid(), gson.toJson(bountyPlayer));
        });
    }

    public static void withBountyPlayer(UUID uuid, Consumer<BountyPlayer> con) {
        BountyPlayer cachedShop = players.getIfPresent(uuid);
        if (cachedShop != null) {
            con.accept(cachedShop);
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(pl, () -> {
            BountyPlayer bountyPlayer = players.getUnchecked(uuid);
            Bukkit.getScheduler().scheduleSyncDelayedTask(pl, () -> con.accept(bountyPlayer));
        });
    }
}
