package gyurix.playershops.data;

import gyurix.cryptidcommons.util.StrUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

import static gyurix.playershops.PlayerShopManager.save;
import static gyurix.playershops.conf.ConfigManager.conf;
import static gyurix.playershops.conf.ConfigManager.msg;

@Getter
@NoArgsConstructor
public class PlayerShop {
    private boolean bought;
    private LinkedHashMap<String, Integer> overflow = new LinkedHashMap<>();
    private UUID owner;
    private long rentedUntil;
    private HashMap<Integer, ShopItem> shopItems = new HashMap<>();

    public PlayerShop(UUID owner) {
        this.owner = owner;
        save(this);
    }

    public void buyNow(Player plr) {
        this.bought = true;
        msg.msg(plr, "permabuy.done");
        save(this);
    }


    public int countCategories() {
        return (int) shopItems.values().stream().filter(shopItem -> shopItem.categoryName != null).count();
    }

    public int countItems() {
        int items = 0;
        for (ShopItem shopItem : shopItems.values())
            items += shopItem.categoryName == null ? 1 : shopItem.subItems.size();
        return items;
    }

    public void extendRent(Player plr, long time) {
        long curTime = System.currentTimeMillis();
        if (rentedUntil > curTime) {
            rentedUntil = Math.min(rentedUntil + time, curTime + StrUtils.toTime(conf.getMaxRentTime()));
            msg.msg(plr, "rent.extend", "time", StrUtils.formatTime(time), "expire", StrUtils.formatTime(rentedUntil - curTime));
        } else {
            rentedUntil = Math.min(curTime + time, curTime + StrUtils.toTime(conf.getMaxRentTime()));
            msg.msg(plr, "rent.done", "time", StrUtils.formatTime(rentedUntil - curTime));
        }
        save(this);
    }


    public void setRentedUntil(long rentedUntil) {
        this.rentedUntil = rentedUntil;
        save(this);
    }
}
