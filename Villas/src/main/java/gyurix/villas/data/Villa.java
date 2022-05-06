package gyurix.villas.data;

import gyurix.cryptidcommons.data.Area;
import gyurix.cryptidcommons.data.Loc;
import lombok.Data;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Function;

import static gyurix.villas.conf.ConfigManager.conf;

@Data
public class Villa {
    private Area area;
    private HashMap<String, Group> groups;
    private String name;
    private Loc spawn;
    private HashMap<UUID, String> players;

    public boolean hasPermission(Player plr, Function<Group, Boolean> permission) {
        String group = players.get(plr.getUniqueId());
        return permission.apply(groups.get(group == null ? conf.getNonMemberGroup() : group));
    }
}
