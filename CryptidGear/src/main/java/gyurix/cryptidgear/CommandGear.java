package gyurix.cryptidgear;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static gyurix.cryptidgear.conf.ConfigManager.msg;

public class CommandGear implements CommandExecutor {
    public void cmdGive(CommandSender sender, String[] args) {

    }

    public void cmdHelp(CommandSender sender) {
        msg.msg(sender, "help");
    }

    public void cmdInfo(CommandSender sender, String[] args) {

    }

    public void cmdList(CommandSender sender) {
        msg.msg(sender, "list");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String string, @NotNull String[] args) {
        if (!sender.hasPermission("cryptidgear.admin")) {
            msg.msg(sender, "noperm");
            return true;
        }
        String sub = args[0].toLowerCase();
        switch (sub) {
            case "help" -> cmdHelp(sender);
            case "give" -> cmdGive(sender, args);
            case "info" -> cmdInfo(sender, args);
            case "list" -> cmdList(sender);
        }
        return true;
    }
}
