package gyurix.villas.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class CommandVillas implements CommandExecutor, TabCompleter {
    private void cmdHelp(CommandSender sender) {

    }

    private void cmdInfo(CommandSender sender, String[] args) {
    }

    private void cmdList(CommandSender sender, String[] args) {

    }

    private void cmdManage(CommandSender sender, String[] args) {
    }

    public void cmdRemove(CommandSender sender, String name) {

    }

    private void cmdTp(CommandSender sender, String[] args) {

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
            case "info" -> {
                cmdInfo(sender, args);
                return true;
            }
            case "tp" -> {
                cmdTp(sender, args);
                return true;
            }
            case "manage" -> {
                cmdManage(sender, args);
                return true;
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String string, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
