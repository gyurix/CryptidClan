package gyurix.bountysystem;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static gyurix.bountysystem.conf.ConfigManager.conf;
import static gyurix.bountysystem.conf.ConfigManager.msg;
import static gyurix.cryptidcommons.util.StrUtils.DF;
import static gyurix.cryptidcommons.util.StrUtils.fillVariables;

public class CommandBounty implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String string, @NotNull String[] args) {
        List<Map.Entry<String, Integer>> bountyList = new ArrayList<>();
        int minStreak = conf.getWrldRewards().ceilingKey(0);
        for (Player p : Bukkit.getOnlinePlayers()) {
            BountyManager.withBountyPlayer(p.getUniqueId(), (bp) -> {
                if (bp.getStreak() >= minStreak)
                    bountyList.add(new AbstractMap.SimpleEntry<>(bp.getName(), bp.getStreak()));
            });
        }
        bountyList.sort(Map.Entry.comparingByValue());
        int maxPage = Math.max(1, (bountyList.size() + 9) / 10);
        int page = 1;
        try {
            page = Integer.parseInt(args[0]);
        } catch (Throwable ignored) {
        }
        page = Math.max(1, Math.min(page, maxPage));
        int to = Math.min(bountyList.size(), page * 10);
        String entryMsg = msg.get("list.entry");
        String headerMsg = msg.get("list.header");
        sender.sendMessage(fillVariables(headerMsg, "page", page, "maxPage", maxPage));
        for (int i = (page - 1) * 10; i < to; ++i) {
            Map.Entry<String, Integer> entry = bountyList.get(i);
            sender.sendMessage(fillVariables(entryMsg,
                    "position", i + 1,
                    "player", entry.getKey(),
                    "streak", entry.getValue(),
                    "bounty", DF.format(conf.wrldRewards.floorEntry(entry.getValue()).getValue())));
        }
        return true;
    }
}
