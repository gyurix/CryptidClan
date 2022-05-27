package gyurix.playershops.cmd;

import com.nftworlds.wallet.objects.NFTPlayer;
import com.nftworlds.wallet.objects.Network;
import com.nftworlds.wallet.objects.Wallet;
import gyurix.cryptidcommons.data.WRLDRunnable;
import gyurix.cryptidcommons.util.StrUtils;
import gyurix.playershops.PlayerShopManager;
import gyurix.playershops.data.PlayerShop;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static gyurix.cryptidcommons.util.StrUtils.filterStart;
import static gyurix.cryptidcommons.util.StrUtils.toTime;
import static gyurix.playershops.PlayerShopsPlugin.pl;
import static gyurix.playershops.conf.ConfigManager.conf;
import static gyurix.playershops.conf.ConfigManager.msg;

public class CommandPlayerShop implements CommandExecutor, TabCompleter {
    private final List<String> subCommands = List.of("buy", "help", "info", "rent", "view");

    public CommandPlayerShop() {
        PluginCommand pcmd = pl.getCommand("playershop");
        pcmd.setExecutor(this);
        pcmd.setTabCompleter(this);
    }

    private void cmdBuy(CommandSender sender) {
        if (!(sender instanceof Player plr)) {
            msg.msg(sender, "noconsole");
            return;
        }
        PlayerShopManager.withShop(plr.getUniqueId(), shop -> {
            if (shop.isBought()) {
                msg.msg(sender, "permabuy.already");
                return;
            }
            Wallet wallet = NFTPlayer.getByUUID(plr.getUniqueId()).getPrimaryWallet();
            wallet.requestWRLD(conf.getPurchasePrice(), Network.POLYGON, "Permanent playershop access", false,
                    (WRLDRunnable) () -> shop.buyNow(plr));
        });
    }

    private void cmdHelp(CommandSender sender) {
        msg.msg(sender, "help.player");
        if (sender.hasPermission("playershops.admin"))
            msg.msg(sender, "help.admin");
    }

    private void cmdInfo(CommandSender sender, String[] args) {
        withPlayerShop(sender, args, (shop) -> {
            long time = System.currentTimeMillis();
            msg.msg(sender, "info",
                    "player", Bukkit.getOfflinePlayer(shop.getOwner()).getName(),
                    "bought", shop.isBought() ? "§ayes" : "§cno",
                    "categories", shop.getShopItem().countCategories(),
                    "items", shop.getShopItem().countItems(),
                    "rented", shop.isBought() || shop.getRentedUntil() < time ? "§cN/A" :
                            StrUtils.formatTime(shop.getRentedUntil() - time));
        });
    }

    private void cmdRent(CommandSender sender, String[] args) {
        if (!(sender instanceof Player plr)) {
            msg.msg(sender, "noconsole");
            return;
        }
        PlayerShopManager.withShop(plr.getUniqueId(), shop -> {
            if (shop.isBought()) {
                msg.msg(sender, "rent.bought.you");
                return;
            }
            long time = toTime(StringUtils.join(args, ' ', 1, args.length));
            if (time < toTime(conf.getMinRentTime())) {
                msg.msg(sender, "rent.toolow", "time", conf.getMinRentTime());
                return;
            }
            long maxTime = toTime(conf.getMaxRentTime());
            if (time > maxTime) {
                msg.msg(sender, "rent.toohigh", "time", conf.getMaxRentTime());
                return;
            }
            long curTime = System.currentTimeMillis();
            if (shop.getRentedUntil() > curTime && shop.getRentedUntil() + time - curTime > maxTime) {
                msg.msg(sender, "rent.toohighextend", "time", conf.getMaxRentTime());
                return;
            }
            Wallet wallet = NFTPlayer.getByUUID(plr.getUniqueId()).getPrimaryWallet();
            wallet.requestWRLD(conf.getRentPricePerDay() / 86400000L * time, Network.POLYGON, "Renting playershop access", false,
                    (WRLDRunnable) () -> shop.extendRent(plr, time));
        });
    }

    private void cmdView(CommandSender sender, String[] args) {

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String string, @NotNull String[] args) {
        String sub = args[0].toLowerCase();
        switch (sub) {
            case "buy" -> cmdBuy(sender);
            case "help" -> cmdHelp(sender);
            case "info" -> cmdInfo(sender, args);
            case "rent" -> cmdRent(sender, args);
            case "view" -> cmdView(sender, args);
            default -> msg.msg(sender, "wrong.sub");
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String string, @NotNull String[] args) {
        if (args.length == 1)
            return filterStart(subCommands, args[0]);
        return null;
    }

    private void withPlayerShop(CommandSender sender, String[] args, Consumer<PlayerShop> con) {
        if (!(sender instanceof Player) && args.length == 1) {
            msg.msg(sender, "missing.player");
            return;
        }
        String target = args.length == 1 ? sender.getName() : args[1];
        UUID uuid = Bukkit.getPlayerUniqueId(target);
        if (uuid == null) {
            msg.msg(sender, "wrong.player", "player", target);
            return;
        }
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (!offlinePlayer.isOnline() && !offlinePlayer.hasPlayedBefore()) {
            msg.msg(sender, "wrong.player", "player", target);
            return;
        }
        PlayerShopManager.withShop(uuid, con);
    }
}
