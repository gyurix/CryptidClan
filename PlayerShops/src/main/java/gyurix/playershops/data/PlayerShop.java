package gyurix.playershops.data;

import gyurix.cryptidcommons.util.ItemUtils;
import gyurix.cryptidcommons.util.StrUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static gyurix.playershops.PlayerShopManager.save;
import static gyurix.playershops.conf.ConfigManager.conf;
import static gyurix.playershops.conf.ConfigManager.msg;

@Getter
@NoArgsConstructor
public class PlayerShop {
    private boolean bought;
    private final TreeSet<String> licenses = new TreeSet<>();
    private final LinkedHashMap<ItemStack, Integer> overflow = new LinkedHashMap<>();
    private UUID owner;
    private long rentedUntil;
    private final ShopItem shopItem = new ShopItem();

    public PlayerShop(UUID owner) {
        this.owner = owner;
        save(this);
    }

    public void buyNow(Player plr) {
        this.bought = true;
        msg.msg(plr, "permabuy.done");
        save(this);
    }

    public void claimItems(Player plr) {
        int am = 0;
        Iterator<Map.Entry<ItemStack, Integer>> it = overflow.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<ItemStack, Integer> entry = it.next();
            ItemStack is = entry.getKey().clone();
            is.setAmount(entry.getValue());
            int remaining = ItemUtils.addItem(plr.getInventory(), is);
            am += entry.getValue() - remaining;
            if (remaining == 0) {
                it.remove();
                continue;
            }
            entry.setValue(remaining);
        }
        msg.msg(plr, am == 0 ? "claim.non" : "claim.done", "amount", am);
    }

    public int countUnclaimedItems() {
        return overflow.values().stream().reduce(Integer::sum).orElse(0);
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

    public String getOwnerName() {
        return Bukkit.getOfflinePlayer(owner).getName();
    }

    public void setBought(boolean bought) {
        this.bought = bought;
        save(this);
    }

    public void setRentedUntil(long rentedUntil) {
        this.rentedUntil = rentedUntil;
        save(this);
    }
}
