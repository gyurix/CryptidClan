package gyurix.playershops.cmd;

import gyurix.cryptidcommons.util.StrUtils;
import gyurix.playershops.PlayerShopManager;
import gyurix.playershops.data.PlayerShop;
import gyurix.playershops.gui.ShopGUI;
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
import static gyurix.playershops.PlayerShopsPlugin.pl;
import static gyurix.playershops.conf.ConfigManager.msg;

public class CommandPlayerShopA implements CommandExecutor, TabCompleter {
    private final List<String> subCommands = List.of("delete", "help", "manage", "setexpiration", "togglebuy");

    public CommandPlayerShopA() {
        PluginCommand pcmd = pl.getCommand("playershopa");
        pcmd.setExecutor(this);
        pcmd.setTabCompleter(this);
    }

    private void cmdDelete(CommandSender sender, String[] args) {
        withPlayerShop(sender, args, (shop) -> {
            PlayerShopManager.delete(shop);
            msg.msg(sender, "delete.shop", "player", shop.getOwnerName());
        });
    }

    private void cmdHelp(CommandSender sender) {
        msg.msg(sender, "help.player");
        if (sender.hasPermission("playershops.admin"))
            msg.msg(sender, "help.admin");
    }

    private void cmdManage(CommandSender sender, String[] args) {
        if (!(sender instanceof Player plr)) {
            msg.msg(sender, "noconsole");
            return;
        }
        withPlayerShop(sender, args, (shop) -> new ShopGUI(plr, shop, true, shop.getShopItem()));
    }

    private void cmdSetExpiration(CommandSender sender, String[] args) {
        withPlayerShop(sender, args, (shop) -> {
            if (shop.isBought()) {
                msg.msg(sender, "rent.bought.others", "player", shop.getOwnerName());
                return;
            }
            long dur = StrUtils.toTime(StringUtils.join(args, ' ', 2, args.length));
            shop.setRentedUntil(System.currentTimeMillis() + dur);
            msg.msg(sender, "rent.setexpiration", "player", shop.getOwnerName(), "time", StrUtils.formatTime(dur));
        });
    }

    private void cmdToggleBuy(CommandSender sender, String[] args) {
        withPlayerShop(sender, args, (shop) -> {
            shop.setBought(!shop.isBought());
            msg.msg(sender, "permabuy." + (shop.isBought() ? "enable" : "disable"), "player", shop.getOwnerName());
        });
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String string, @NotNull String[] args) {
        if (!sender.hasPermission("playershop.admin")) {
            msg.msg(sender, "noperm");
            return true;
        }
        String sub = args.length == 0 ? "help" : args[0].toLowerCase();
        switch (sub) {
            case "delete" -> cmdDelete(sender, args);
            case "help" -> cmdHelp(sender);
            case "manage" -> cmdManage(sender, args);
            case "setexpiration" -> cmdSetExpiration(sender, args);
            case "togglebuy" -> cmdToggleBuy(sender, args);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String string, @NotNull String[] args) {
        if (args.length == 1)
            return filterStart(subCommands, args[0]);
        if (args.length == 2)
            return null;
        return List.of();
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
