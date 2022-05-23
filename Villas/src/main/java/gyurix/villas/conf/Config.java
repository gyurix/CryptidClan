package gyurix.villas.conf;

import gyurix.cryptidcommons.conf.PostProcessable;
import gyurix.cryptidcommons.db.MySQLDatabase;
import gyurix.cryptidcommons.gui.GUIConfig;
import gyurix.villas.data.Group;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

@Getter
public class Config implements PostProcessable {
    private Material customGroupIcon;
    private ItemStack defaultVillaIcon;
    private HashMap<String, Group> groups;
    private HashMap<String, GUIConfig> guis;
    private MySQLDatabase mySQL;
    private String newMemberGroup, nonMemberGroup, villasBuy, villasManage, villasTp;
    private int playerLimit, groupLimit;
    private List<String> playersLore, groupsLore, groupsSelectedLore;
    private String playersName, groupsName, groupsSelectedName;

    @Override
    public void postProcess() {
        groups.forEach((name, group) -> group.setName(name));
    }
}
