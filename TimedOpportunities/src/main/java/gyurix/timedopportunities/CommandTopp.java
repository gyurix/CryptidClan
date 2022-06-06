package gyurix.timedopportunities;

import gyurix.cryptidcommons.data.BlockLoc;
import gyurix.cryptidcommons.util.StrUtils;
import gyurix.timedopportunities.conf.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static gyurix.timedopportunities.LandingManager.currentLoc;
import static gyurix.timedopportunities.conf.ConfigManager.conf;
import static gyurix.timedopportunities.conf.ConfigManager.msg;

public class CommandTopp implements CommandExecutor {
    private void cmdAddLoc(CommandSender sender) {
        if (!(sender instanceof Player plr)) {
            msg.msg(sender, "noconsole");
            return;
        }
        BlockLoc blockLoc = new BlockLoc(plr.getLocation().getBlock());
        conf.locations.add(blockLoc);
        ConfigManager.saveLocations();
        msg.msg(sender, "addloc", "loc", blockLoc);
    }

    private void cmdHelp(CommandSender sender) {
        msg.msg(sender, "help");
    }

    private void cmdInfo(CommandSender sender) {
        msg.msg(sender, "info",
                "loc", currentLoc == null ? "N/A" : currentLoc,
                "next", StrUtils.formatTime(LandingManager.nextLanding - System.currentTimeMillis()));
    }

    private void cmdStart(CommandSender sender) {
        LandingManager.scheduleNextLanding(true);
        msg.msg(sender, "start");
    }

    private void cmdStop(CommandSender sender) {
        LandingManager.stop();
        msg.msg(sender, "stop");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String string, @NotNull String[] args) {
        if (!(sender.hasPermission("timedopportunities.admin"))) {
            cmdInfo(sender);
            return true;
        }
        String sub = args.length == 1 ? args[0].toLowerCase() : "help";
        switch (sub) {
            case "addloc" -> cmdAddLoc(sender);
            case "help" -> cmdHelp(sender);
            case "info" -> cmdInfo(sender);
            case "start" -> cmdStart(sender);
            case "stop" -> cmdStop(sender);
        }
        return true;
    }
}
