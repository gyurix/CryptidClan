package gyurix.villas.cmd;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.regions.Region;
import gyurix.cryptidcommons.data.Area;
import gyurix.cryptidcommons.data.Loc;
import gyurix.cryptidcommons.util.StrUtils;
import gyurix.villas.VillaManager;
import gyurix.villas.data.Villa;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static gyurix.villas.VillasPlugin.pl;
import static gyurix.villas.conf.ConfigManager.msg;

public class CommandVillasA implements CommandExecutor, TabCompleter {

    List<String> subCommands = List.of("create", "help", "list", "redefine", "remove");

    public CommandVillasA() {
        PluginCommand pcmd = pl.getCommand("villasa");
        pcmd.setExecutor(this);
        pcmd.setTabCompleter(this);
    }

    private void cmdCreate(CommandSender sender, String[] args) {
        if (args.length == 1) {
            msg.msg(sender, "missing.villa");
            return;
        }
        String name = args[1].toLowerCase();
        if (VillaManager.villas.containsKey(name)) {
            msg.msg(sender, "admin.exists", "villa", name);
            return;
        }
        withArea(sender, area -> {
            Location spawn = ((Player) sender).getLocation();
            if (!area.contains(spawn)) {
                msg.msg(sender, "wrong.spawn", "villa", name);
                return;
            }
            Villa villa = new Villa(name, area, new Loc(spawn));
            VillaManager.villas.put(name, villa);
            VillaManager.saveVilla(villa);
            msg.msg(sender, "admin.create", "villa", name);
        });
    }

    private void cmdHelp(CommandSender sender) {
        msg.msg(sender, "help.header");
        msg.msg(sender, "help.admin");
        msg.msg(sender, "help.player");
    }

    private void cmdRedefine(CommandSender sender, String[] args) {
        withVilla(sender, args, villa -> {
            withArea(sender, area -> {
                villa.setArea(area);
                msg.msg(sender, "admin.redefine", "villa", villa.getName(), "area", villa.getArea());
            });
        });
    }

    private void cmdRemove(CommandSender sender, String[] args) {
        withVilla(sender, args, villa -> villa.remove(sender));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String string, @NotNull String[] args) {
        if (!sender.hasPermission("villas.admin")) {
            msg.msg(sender, "noperm");
            return true;
        }
        String sub = args.length == 0 ? "help" : args[0].toLowerCase();
        switch (sub) {
            case "create" -> {
                cmdCreate(sender, args);
                return true;
            }
            case "help" -> {
                cmdHelp(sender);
                return true;
            }
            case "redefine" -> {
                cmdRedefine(sender, args);
                return true;
            }
            case "remove" -> {
                cmdRemove(sender, args);
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

    private void withArea(CommandSender sender, Consumer<Area> con) {
        if (!(sender instanceof Player)) {
            msg.msg(sender, "noconsole");
            return;
        }
        Region region = null;
        BukkitPlayer bPlayer = BukkitAdapter.adapt((Player) sender);
        try {
            region = WorldEdit.getInstance().getSessionManager().get(bPlayer).getSelection(bPlayer.getWorld());
        } catch (Throwable ignored) {
        }
        if (region == null) {
            msg.msg(sender, "missing.sel");
            return;
        }
        Area area = new Area(bPlayer.getWorld().getName(), region);
        con.accept(area);
    }

    private void withVilla(CommandSender sender, String[] args, Consumer<Villa> con) {
        boolean villaArg = args.length > 1;
        if (!(sender instanceof Player) && !villaArg) {
            msg.msg(sender, "missing.villa");
            return;
        }
        Villa villa = villaArg ? VillaManager.villas.get(args[1].toLowerCase()) : VillaManager.getVillaAt(((Player) sender).getLocation());
        if (villa == null) {
            if (villaArg) {
                msg.msg(sender, "wrong.villa", "villa", args[1].toLowerCase());
                return;
            }
            msg.msg(sender, "wrong.villaLoc");
            return;
        }
        con.accept(villa);
    }
}
