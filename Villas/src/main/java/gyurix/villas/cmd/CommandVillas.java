package gyurix.villas.cmd;

import gyurix.cryptidcommons.data.Loc;
import gyurix.cryptidcommons.util.StrUtils;
import gyurix.villas.VillaManager;
import gyurix.villas.data.Group;
import gyurix.villas.data.Villa;
import gyurix.villas.gui.ManageGUI;
import gyurix.villas.gui.VillasGUI;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static gyurix.cryptidcommons.util.StrUtils.DF;
import static gyurix.villas.VillasPlugin.pl;
import static gyurix.villas.conf.ConfigManager.msg;

public class CommandVillas implements CommandExecutor, TabCompleter {
    List<String> subCommands = List.of("help", "info", "list", "manage", "setspawn", "tp");

    public CommandVillas() {
        PluginCommand pcmd = pl.getCommand("villas");
        pcmd.setExecutor(this);
        pcmd.setTabCompleter(this);
    }

    private void cmdGui(CommandSender sender) {
        if (!(sender instanceof Player plr)) {
            msg.msg(sender, "noconsole");
            return;
        }
        new VillasGUI(plr);
    }

    private void cmdHelp(CommandSender sender) {
        msg.msg(sender, "help.header");
        if (sender.hasPermission("villas.admin"))
            msg.msg(sender, "help.admin");
        msg.msg(sender, "help.player");
    }

    private void cmdInfo(CommandSender sender, String[] args) {
        withVilla(sender, args, Group::isInfo, villa -> {
                    msg.msg(sender, "info.header",
                            "villa", villa.getName(),
                            "area", villa.getArea(),
                            "buyable", villa.isBuyable() ? "§ayes" : "§cno",
                            "price", DF.format(villa.getPrice()),
                            "spawn", villa.getSpawn());
                    villa.getGrouppedPlayerNames().forEach((group, players) ->
                            msg.msg(sender, "info.player",
                                    "group", group,
                                    "count", players.size(),
                                    "players", StringUtils.join(players, ", ")));
                },
                villa -> msg.msg(sender, "noperm.info"));
    }

    private void cmdList(CommandSender sender, String[] args) {
        withPlayer(sender, args, (target) -> {
            int page = 1;
            try {
                if (args.length > 1 && args[args.length - 1].matches("\\d"))
                    page = Integer.parseInt(args[args.length - 1]);
            } catch (Throwable ignored) {
            }
            List<Villa> results = VillaManager.getVillasWithPlayer(sender, target.getUniqueId());
            int maxpage = Math.max(1, (results.size() + 9) / 10);
            page = Math.min(Math.max(page, 1), maxpage);
            msg.msg(sender, "list.header", "player", target.getName(), "page", page, "maxpage", maxpage);
            int from = (page - 1) * 10;
            int to = Math.min(from + 10, results.size());
            for (int i = from; i < to; ++i) {
                Villa v = results.get(i);
                msg.msg(sender, v.isBuyable() ? "list.buyableEntry" : "list.entry",
                        "villa", v.getName(),
                        "players", v.getPlayers().size(),
                        "price", DF.format(v.getPrice()),
                        "rank", v.getPlayers().get(target.getUniqueId()));
            }
        });
    }

    private void cmdListAll(CommandSender sender, String[] args) {
        int page = 1;
        try {
            if (args.length > 1 && args[args.length - 1].matches("\\d"))
                page = Integer.parseInt(args[args.length - 1]);
        } catch (Throwable ignored) {
        }
        List<Villa> results = new ArrayList<>(VillaManager.villas.values());
        if (!sender.hasPermission("villas.admin"))
            results.removeIf(villa -> !villa.hasPermission(sender, Group::isSee, false));
        int maxpage = Math.max(1, (results.size() + 9) / 10);
        page = Math.min(Math.max(page, 1), maxpage);
        msg.msg(sender, "listall.header", "page", page, "maxpage", maxpage);
        int from = (page - 1) * 10;
        int to = Math.min(from + 10, results.size());
        for (int i = from; i < to; ++i) {
            Villa v = results.get(i);
            msg.msg(sender, v.isBuyable() ? "listall.buyableEntry" : "listall.entry",
                    "villa", v.getName(),
                    "players", v.getPlayers().size(),
                    "price", DF.format(v.getPrice()));
        }
    }

    private void cmdManage(CommandSender sender, String[] args) {
        if (!(sender instanceof Player plr)) {
            msg.msg(sender, "noconsole");
            return;
        }
        withVilla(sender, args, Group::isInfo, villa -> new ManageGUI(plr, villa),
                villa -> msg.msg(sender, "noperm.info"));
    }

    private void cmdSetSpawn(CommandSender sender, String[] args) {
        if (!(sender instanceof Player plr)) {
            msg.msg(sender, "noconsole");
            return;
        }
        withVilla(sender, args, Group::isManage, villa -> {
                    Location spawn = plr.getLocation();
                    if (!villa.getArea().contains(spawn)) {
                        msg.msg(sender, "wrong.spawn", "villa", villa.getName());
                        return;
                    }
                    villa.setSpawn(new Loc(spawn));
                    VillaManager.saveVilla(villa);
                    msg.msg(sender, "setspawn", "villa", villa.getName(), "loc", villa.getSpawn());
                },
                villa -> msg.msg(sender, "villa.noperm.tp"));
    }

    private void cmdTp(CommandSender sender, String[] args) {
        if (!(sender instanceof Player plr)) {
            msg.msg(sender, "noconsole");
            return;
        }
        withVilla(sender, args, Group::isTp, villa -> {
                    plr.teleport(villa.getSpawn().toLocation());
                    msg.msg(sender, "tp", "villa", villa.getName());
                },
                villa -> msg.msg(sender, "villa.noperm.tp"));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String string, @NotNull String[] args) {
        String sub = args.length == 0 ? "gui" : args[0].toLowerCase();
        switch (sub) {
            case "gui" -> {
                cmdGui(sender);
                return true;
            }
            case "help" -> {
                cmdHelp(sender);
                return true;
            }
            case "list" -> {
                cmdList(sender, args);
                return true;
            }
            case "listall" -> {
                cmdListAll(sender, args);
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
            case "setspawn" -> {
                cmdSetSpawn(sender, args);
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

    private void withPlayer(CommandSender sender, String[] args, Consumer<OfflinePlayer> con) {
        Player plr = sender instanceof Player ? (Player) sender : null;
        boolean hasPlayerArg = args.length > 1 && !args[1].matches("\\d+");
        if (plr == null && !hasPlayerArg) {
            msg.msg(sender, "missing.player");
            return;
        }
        OfflinePlayer target = hasPlayerArg ? Bukkit.getOfflinePlayer(args[1]) : plr;
        if (target.hasPlayedBefore() || target.isOnline()) {
            con.accept(target);
            return;
        }
        msg.msg(sender, "wrong.player", "player", args[1]);
    }

    private void withVilla(CommandSender sender, String[] args, Function<Group, Boolean> extraPerm, Consumer<Villa> con, Consumer<Villa> noPerm) {
        boolean villaArg = args.length > 1;
        if (!(sender instanceof Player) && !villaArg) {
            msg.msg(sender, "missing.villa");
            return;
        }
        Villa villa = villaArg ? VillaManager.villas.get(args[1].toLowerCase()) : VillaManager.getVillaAt(((Player) sender).getLocation());
        if (villa == null || !villa.hasPermission(sender, Group::isSee)) {
            if (villaArg) {
                msg.msg(sender, "wrong.villa", "villa", args[1].toLowerCase());
                return;
            }
            msg.msg(sender, "wrong.villaLoc");
            return;
        }
        if (villa.hasPermission(sender, extraPerm)) {
            con.accept(villa);
            return;
        }
        noPerm.accept(villa);
    }
}
