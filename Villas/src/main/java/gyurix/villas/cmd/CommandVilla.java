package gyurix.villas.cmd;

import gyurix.villas.VillaManager;
import gyurix.villas.data.Group;
import gyurix.villas.data.Villa;
import gyurix.villas.gui.ManageGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static gyurix.villas.conf.ConfigManager.msg;

public class CommandVilla implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String string, @NotNull String[] args) {
        if (!(sender instanceof Player plr)) {
            msg.msg(sender, "noconsole");
            return true;
        }
        Villa villa = VillaManager.getVillaAt(plr.getLocation());
        boolean admin = sender.hasPermission("villas.admin");
        if (villa != null && (villa.hasPermission(sender, Group::isInfo) || admin)) {
            new ManageGUI(plr, villa);
            return true;
        }
        List<Villa> villas = VillaManager.getVillasWithPlayer(sender, plr.getUniqueId());
        if (villas.isEmpty()) {
            msg.msg(sender, "notinany");
            return true;
        }
        if (villas.size() == 1) {
            villa = villas.get(0);
            if (!admin && !villa.hasPermission(sender, Group::isTp)) {
                msg.msg(sender, "noperm.tp", "villa", villa.getName());
                return true;
            }
            plr.teleport(villa.getSpawn().toLocation());
            msg.msg(sender, "tp", "villa", villa.getName());
            return true;
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String string, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
