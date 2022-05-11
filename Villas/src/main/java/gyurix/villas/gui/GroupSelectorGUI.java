package gyurix.villas.gui;

import gyurix.cryptidcommons.gui.CustomGUI;
import gyurix.cryptidcommons.util.ItemUtils;
import gyurix.cryptidcommons.util.StrUtils;
import gyurix.villas.data.Group;
import gyurix.villas.data.Villa;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.TreeSet;

import static gyurix.villas.conf.ConfigManager.conf;
import static gyurix.villas.conf.ConfigManager.msg;

public class GroupSelectorGUI extends CustomGUI {
    private final HashMap<Integer, String> groupNames = new HashMap<>();
    private final String target;
    private final Villa villa;

    public GroupSelectorGUI(Player plr, Villa villa, String target) {
        super(plr, conf.getGuis().get("playerGroupSelector"));
        this.villa = villa;
        this.target = target;

        String title = StrUtils.fillVariables(config.getTitle(), "player", target, "villa", villa.getName());
        int rows = (villa.getGroups().size() + 8) / 9;
        inv = Bukkit.createInventory(this, config.getLayout().size() + rows * 9, title);

        config.getStaticMap().forEach((slot, is) -> inv.setItem(slot, is));
        update();
        plr.openInventory(inv);
    }

    @Override
    public void onClick(int slot, boolean right, boolean shift) {
        if (slot >= inv.getSize() || slot < 0)
            return;
        String type = config.getLayout().get(slot);
        if (type.equals("exit")) {
            plr.closeInventory();
            return;
        } else if (type.equals("back")) {
            new PlayersGUI(plr, villa);
            return;
        }
        String groupName = groupNames.get(slot);
        if (groupName == null)
            return;
        if (!villa.hasPermission(plr, Group::isManage)) {
            msg.msg(plr, "noperm.manage");
            return;
        }
        villa.changeGroup(plr, target, groupName);
        new PlayersGUI(plr, villa);
    }

    @Override
    public void update() {
        int id = 0;
        for (int i = 9; i < inv.getSize(); ++i)
            inv.setItem(i, config.getStaticItem("glass"));
        String playerGroup = villa.getPlayers().get(Bukkit.getPlayerUniqueId(target));
        for (String groupName : new TreeSet<>(villa.getGroups().keySet())) {
            boolean selected = groupName.equals(playerGroup);
            ItemStack is = ItemUtils.makeItem(villa.getGroups().get(groupName).getIcon(),
                    StrUtils.fillVariables(selected ? conf.getGroupsSelectedName() : conf.getGroupsName(), "name", groupName),
                    selected ? conf.getGroupsSelectedLore() : conf.getGroupsLore());
            inv.setItem(dataSlots[id], selected ? ItemUtils.glow(is) : is);
            groupNames.put(dataSlots[id], groupName);
            ++id;
        }
    }
}
