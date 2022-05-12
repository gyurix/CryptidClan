package gyurix.villas.cmd;

import gyurix.cryptidcommons.util.StrUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static gyurix.villas.conf.ConfigManager.msg;

public class CommandVillasA implements CommandExecutor, TabCompleter {

    List<String> subCommands = List.of("addmember", "create", "help", "list", "redefine", "remove", "removemember", "setspawn");

    private void cmdHelp(CommandSender sender) {
        msg.msg(sender, "help.admin");
    }


    private void cmdList(CommandSender sender, String[] args) {

    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String string, @NotNull String[] args) {
        String sub = args.length == 0 ? "help" : args[0].toLowerCase();
        switch (sub) {
            case "help" -> {
                cmdHelp(sender);
                return true;
            }
            case "list" -> {
                cmdList(sender, args);
                return true;
            }
        }
        msg.msg(sender, "wrong.sub");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String string, @NotNull String[] args) {
        if (args.length == 1)
            return StrUtils.filterStart(subCommands, args[0]);
        return Collections.emptyList();
    }
}
