package gyurix.playershops.cmd;

import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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

    }

    private void cmdHelp(CommandSender sender) {
        msg.msg(sender, "help.player");
        if (sender.hasPermission("playershops.admin"))
            msg.msg(sender, "help.admin");
    }

    private void cmdManage(CommandSender sender, String[] args) {

    }

    private void cmdSetExpiration(CommandSender sender, String[] args) {

    }

    private void cmdToggleBuy(CommandSender sender, String[] args) {

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String string, @NotNull String[] args) {
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
        return null;
    }
}
