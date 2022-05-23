package gyurix.villas.data;

import com.nftworlds.wallet.objects.NFTPlayer;
import com.nftworlds.wallet.objects.Network;
import com.nftworlds.wallet.objects.Wallet;
import gyurix.cryptidcommons.data.Area;
import gyurix.cryptidcommons.data.Loc;
import gyurix.cryptidcommons.data.WRLDRunnable;
import gyurix.cryptidcommons.util.ItemUtils;
import gyurix.villas.VillaManager;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Function;

import static gyurix.cryptidcommons.util.StrUtils.DF;
import static gyurix.villas.conf.ConfigManager.conf;
import static gyurix.villas.conf.ConfigManager.msg;

@Data
@NoArgsConstructor
public class Villa {
    private Area area;
    private HashMap<String, Group> groups = new HashMap<>();
    private String name;
    private HashMap<UUID, String> players = new HashMap<>();
    private Loc spawn;

    private String icon;

    private boolean buyable;
    private double price;

    public Villa(String name, Area area, Loc spawn) {
        this.name = name;
        this.area = area;
        this.spawn = spawn;
        this.icon = ItemUtils.itemToString(conf.getDefaultVillaIcon());
        conf.getGroups().values().forEach(group -> this.groups.put(group.getName(), group.clone()));
    }

    public List<UUID> getOwners() {
        List<UUID> out = new ArrayList<>();
        players.forEach((uuid, group) -> {
            if (group.equals("owner"))
                out.add(uuid);
        });
        return out;
    }

    public void addGroup(CommandSender sender, String groupName) {
        groupName = groupName.toLowerCase();
        if (!hasPermission(sender, Group::isManage)) {
            msg.msg(sender, "noperm.manage");
            return;
        }
        if (groups.containsKey(groupName)) {
            msg.msg(sender, "group.already", "group", groupName, "villa", name);
            return;
        }
        if (groups.size() >= conf.getGroupLimit()) {
            msg.msg(sender, "group.limit", "group", groupName, "villa", name);
            return;
        }
        Group group = groups.get(conf.getNewMemberGroup()).clone();
        group.setIcon(conf.getCustomGroupIcon());
        group.setName(groupName);
        group.setRemovable(true);
        groups.put(groupName, group);
        msg.msg(sender, "group.create", "group", groupName, "villa", name);
        VillaManager.saveVilla(this);
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
            msg.msg(sender, "player.already", "player", p.getName(), "villa", name);
            return;
        }
        if (players.size() >= conf.getPlayerLimit()) {
            msg.msg(sender, "player.limit", "player", p.getName(), "villa", name);
            return;
        }
        msg.msg(sender, "player.add", "player", p.getName(), "villa", name);
        msg.msg(p, "player.added", "villa", name);
        players.put(p.getUniqueId(), conf.getNewMemberGroup());
        VillaManager.saveVilla(this);
    }

    public void changeGroup(CommandSender sender, String pln, String group) {
        if (!hasPermission(sender, Group::isManage)) {
            msg.msg(sender, "noperm.manage");
            return;
        }
        if (!groups.containsKey(group)) {
            msg.msg(sender, "wrong.group", "group", group, "villa", name);
            return;
        }
        UUID uuid = Bukkit.getPlayerUniqueId(pln);
        String curGroup = players.get(uuid);
        if (curGroup == null) {
            msg.msg(sender, "notin", "player", pln, "group", group, "villa", name);
            return;
        }
        if (curGroup.equals("owner") && Collections.frequency(players.values(), "owner") == 1) {
            msg.msg(sender, "group.lastowner", "player", pln, "villa", name);
            return;
        }
        players.put(uuid, group);
        msg.msg(sender, "groupch", "player", pln, "group", group, "villa", name);
        Player p = Bukkit.getPlayer(uuid);
        if (p != null)
            msg.msg(p, "groupchd", "group", group, "villa", name);

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
        return hasPermission(sender, permission, true);
    }

    public boolean hasPermission(CommandSender sender, Function<Group, Boolean> permission, boolean adminCheck) {
        if (!(sender instanceof Player plr))
            return true;
        String group = players.get(plr.getUniqueId());
        return permission.apply(groups.get(group == null ? conf.getNonMemberGroup() : group)) || adminCheck && sender.hasPermission("villas.admin");
    }

    public boolean kick(CommandSender sender, String pln) {
        UUID uuid = Bukkit.getOfflinePlayer(pln).getUniqueId();
        String curGroup = players.get(uuid);
        if (curGroup.equals("owner") && Collections.frequency(players.values(), "owner") == 1) {
            msg.msg(sender, "group.lastowner", "player", pln, "villa", name);
            return false;
        }
        if (players.remove(uuid) == null) {
            msg.msg(sender, "notin", "player", pln, "villa", name);
            return false;
        }
        msg.msg(sender, "remove", "player", pln, "villa", name);
        Player p = Bukkit.getPlayer(uuid);
        if (p != null)
            msg.msg(p, "removed", "villa", name);
        VillaManager.saveVilla(this);
        return true;
    }

    public void remove(CommandSender sender) {
        for (UUID uuid : players.keySet()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null)
                msg.msg(p, "admin.remove", "villa", name);
        }
        msg.msg(sender, "admin.removed", "villa", name);
        VillaManager.removeVilla(this);
    }

    public void removeGroup(CommandSender sender, String groupName) {
        if (!hasPermission(sender, Group::isManage)) {
            msg.msg(sender, "noperm.manage");
            return;
        }
        groupName = groupName.toLowerCase();
        Group group = groups.get(groupName);
        if (group == null) {
            msg.msg(sender, "wrong.group", "group", groupName, "villa", name);
            return;
        }
        if (!group.isRemovable()) {
            msg.msg(sender, "group.notremovable", "group", groupName);
            return;
        }
        if (players.containsValue(groupName)) {
            msg.msg(sender, "group.notempty", "group", group.getName());
            return;
        }
        groups.remove(groupName);
        msg.msg(sender, "group.remove", "group", group.getName());
        VillaManager.saveVilla(this);
    }

    public void buy(Player plr) {
        if (!buyable) {
            msg.msg(plr, "buyable.non", "villa", name);
            return;
        }
        if (price == 0 || plr.hasPermission("villas.free")) {
            buyNow(plr, price);
            return;
        }
        Wallet wallet = NFTPlayer.getByUUID(plr.getUniqueId()).getPrimaryWallet();
        wallet.requestWRLD(price, Network.POLYGON, "Buying villa " + name, false,
                (WRLDRunnable) () -> buyNow(plr, price));
    }

    private void buyNow(Player plr, double price) {
        List<UUID> owners = getOwners();
        if (!owners.isEmpty()) {
            double split = price / owners.size();
            for (UUID uuid : owners) {
                Wallet wallet = NFTPlayer.getByUUID(uuid).getPrimaryWallet();
                wallet.payWRLD(split, Network.POLYGON, "Selling villa " + name);
                msg.msg(plr, "buyable.sell", "villa", name, "price", DF.format(price), "split", DF.format(split));
                Group newMembersGroup = groups.get(conf.getNewMemberGroup());
                newMembersGroup.setManage(false);
                players.put(uuid, conf.getNewMemberGroup());
            }
        }
        players.put(plr.getUniqueId(), "owner");
        msg.msg(plr, "buyable.buy", "price", DF.format(price), "villa", name);
        VillaManager.saveVilla(this);
    }
}
