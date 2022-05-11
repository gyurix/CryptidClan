package gyurix.villas.data;

import gyurix.cryptidcommons.data.Area;
import gyurix.cryptidcommons.data.Loc;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static gyurix.villas.conf.ConfigManager.conf;
import static gyurix.villas.conf.ConfigManager.msg;

@Data
public class Villa {
    private Area area;
    private HashMap<String, Group> groups;
    private String name;
    private HashMap<UUID, String> players;
    private Loc spawn;

    public void changeGroup(CommandSender sender, String pln, String group) {
        if (!groups.containsKey(group)) {
            msg.msg(sender, "wrong.group", "group", group, "villa", name);
            return;
        }
        UUID uuid = Bukkit.getPlayerUniqueId(pln);
        String curGroup = players.get(uuid);
        if (curGroup == null) {
            msg.msg(sender, "notin", "player", pln, "villa", name);
            return;
        }
        if (curGroup.equals("owner") && Collections.frequency(players.values(), "owner") == 1) {
            msg.msg(sender, "group.lastowner", "player", pln, "villa", name);
            return;
        }
        players.put(uuid, group);
        msg.msg(sender, "groupch", "player", pln, "villa", name);
        Player p = Bukkit.getPlayer(uuid);
        if (p != null)
            msg.msg(p, "groupchd", "villa", name);
    }

    public List<String> getPlayerNames() {
        List<String> out = new ArrayList<>();
        for (UUID uuid : players.keySet()) {
            out.add(Bukkit.getOfflinePlayer(uuid).getName());
        }
        Collections.sort(out);
        return out;
    }

    public boolean hasPermission(Player plr, Function<Group, Boolean> permission) {
        String group = players.get(plr.getUniqueId());
        return permission.apply(groups.get(group == null ? conf.getNonMemberGroup() : group));
    }

    public void kick(CommandSender sender, String pln) {
        UUID uuid = Bukkit.getOfflinePlayer(pln).getUniqueId();
        if (players.remove(uuid) == null) {
            msg.msg(sender, "notin", "player", pln, "villa", name);
            return;
        }
        msg.msg(sender, "remove", "player", pln, "villa", name);
        Player p = Bukkit.getPlayer(uuid);
        if (p != null) {
            msg.msg(p, "removed", "villa", name);
        }
    }
}
