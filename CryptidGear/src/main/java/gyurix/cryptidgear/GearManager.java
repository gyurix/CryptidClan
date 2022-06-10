package gyurix.cryptidgear;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import gyurix.cryptidgear.data.PlayerData;
import gyurix.cryptidgear.data.Weapon;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class GearManager {
    public static LoadingCache<UUID, PlayerData> playerData = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {
                @Override
                public PlayerData load(UUID uuid) {
                    return new PlayerData(uuid);
                }
            });

    public static HashMap<String, Weapon> weaponCache = new HashMap<>();

    public static Weapon getWeapon(ItemStack is) {
        if (is == null || !is.hasItemMeta() || !is.getItemMeta().hasDisplayName())
            return null;

        return weaponCache.get(is.getItemMeta().getDisplayName());
    }
}
