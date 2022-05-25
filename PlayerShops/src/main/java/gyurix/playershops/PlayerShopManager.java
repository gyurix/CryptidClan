package gyurix.playershops;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import gyurix.playershops.data.PlayerShop;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static gyurix.playershops.PlayerShopsPlugin.pl;
import static gyurix.playershops.conf.ConfigManager.conf;
import static gyurix.playershops.conf.ConfigManager.gson;

public class PlayerShopManager {
    private static LoadingCache<UUID, PlayerShop> playerShops = CacheBuilder
            .newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {
                @Override
                public PlayerShop load(@NotNull UUID uuid) {
                    AtomicReference<PlayerShop> out = new AtomicReference<>();
                    conf.getMySQL().query("SELECT `data` FROM `" + conf.getMySQL().table + "` WHERE `owner` = ? LIMIT 1", (rs) -> {
                        if (rs.next())
                            out.set(gson.fromJson(rs.getString(1), PlayerShop.class));
                    }, uuid);
                    if (out.get() == null)
                        out.set(new PlayerShop(uuid));
                    return out.get();
                }
            });

    public static void delete(PlayerShop shop) {
        playerShops.invalidate(shop.getOwner());
        Bukkit.getScheduler().runTaskAsynchronously(pl, () ->
                conf.getMySQL().command("DELETE FROM `" + conf.getMySQL().table + "` WHERE `owner` = ? LIMIT 1", shop.getOwner()));
    }

    public static void initTable() {
        Bukkit.getScheduler().runTaskAsynchronously(pl,
                () -> conf.getMySQL().command("CREATE TABLE IF NOT EXISTS `" + conf.getMySQL().table + "` (`owner` CHAR(40) UNIQUE PRIMARY KEY, `data` MEDIUMTEXT)"));

    }

    public static void save(PlayerShop shop) {
        Bukkit.getScheduler().runTaskAsynchronously(pl, () -> {
            int updated = conf.getMySQL().update("UPDATE `" + conf.getMySQL().table + "` SET `data` = ? WHERE `owner` = ?", gson.toJson(shop), shop.getOwner());
            if (updated == 0)
                conf.getMySQL().update("INSERT INTO `" + conf.getMySQL().table + "` VALUES ( ?, ? )", shop.getOwner(), gson.toJson(shop));
        });
    }

    public static void withShop(UUID uuid, Consumer<PlayerShop> con) {
        PlayerShop cachedShop = playerShops.getIfPresent(uuid);
        if (cachedShop != null) {
            con.accept(cachedShop);
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(pl, () -> {
            PlayerShop shop = playerShops.getUnchecked(uuid);
            Bukkit.getScheduler().scheduleSyncDelayedTask(pl, () -> {
                con.accept(shop);
            });
        });
    }
}
