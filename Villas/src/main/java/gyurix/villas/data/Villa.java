package gyurix.villas.data;

import gyurix.cryptidcommons.data.Area;
import gyurix.cryptidcommons.data.Loc;
import gyurix.villas.VillaManager;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.Function;

import static gyurix.villas.conf.ConfigManager.conf;
import static gyurix.villas.conf.ConfigManager.msg;

@Data
@NoArgsConstructor
public class Villa {
    private Area area;
    private HashMap<String, Group> groups;
    private String name;
    private HashMap<UUID, String> players;
    private Loc spawn;

    public Villa(String name, Area area) {
        this.name = name;
        this.area = area;
    }

    public void addPlayer(CommandSender sender, String pln) {
        if (!hasPermission(sender, Group::isManage)) {
            msg.msg(sender, "noperm.manage");
            return;
        }
        Player p = Bukkit.getPlayer(pln);
        if (p == null) {
            msg.msg(sender, "wrong.player", "player", pln);
            return;
        }
        if (players.containsKey(p.getUniqueId())) {
            msg.msg(sender, "addalready", "player", p.getName(), "villa", name);
            return;
        }
        msg.msg(sender, "add", "player", p.getName(), "villa", name);
        msg.msg(p, "added", "villa", name);
        players.put(p.getUniqueId(), conf.getNewMemberGroup());
        VillaManager.saveVilla(this);
    }

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

        VillaManager.saveVilla(this);
    }

    public Map<String, TreeSet<String>> getGrouppedPlayerNames() {
        TreeMap<String, TreeSet<String>> out = new TreeMap<>();
        players.forEach((uuid, group) -> {
            TreeSet<String> list = out.computeIfAbsent(group, k -> new TreeSet<>());
            list.add(Bukkit.getOfflinePlayer(uuid).getName());
        });
        return out;
    }

    public List<String> getPlayerNames() {
        List<String> out = new ArrayList<>();
        for (UUID uuid : players.keySet()) {
            out.add(Bukkit.getOfflinePlayer(uuid).getName());
        }
        Collections.sort(out);
        return out;
    }

    public boolean hasPermission(CommandSender sender, Function<Group, Boolean> permission) {
        if (!(sender instanceof Player plr))
            return true;
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
        VillaManager.saveVilla(this);
    }

    public void remove(CommandSender sender) {
        for (UUID uuid : players.keySet()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null)
                msg.msg(p, "admin.remove", "villa", name);
        }
        msg.msg(sender, "admin.removed");
        VillaManager.removeVilla(this);
    }
}
