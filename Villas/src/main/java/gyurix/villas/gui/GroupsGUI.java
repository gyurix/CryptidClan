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

public class GroupsGUI extends CustomGUI {
    private final HashMap<Integer, String> groupNames = new HashMap<>();
    private final Villa villa;

    public GroupsGUI(Player plr, Villa villa) {
        super(plr, conf.getGuis().get("groups"));
        this.villa = villa;

        String title = StrUtils.fillVariables(config.getTitle(), "villa", villa.getName());
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
        switch (type) {
            case "exit" -> {
                plr.closeInventory();
                return;
            }
            case "back" -> {
                new PlayersGUI(plr, villa);
                return;
            }
            case "add" -> {
                return;
            }
        }
        String groupName = groupNames.get(slot);
        if (groupName == null)
            return;
        Group group = villa.getGroups().get(groupName);
        if (group == null) {
            new GroupsGUI(plr, villa);
            return;
        }
        new GroupEditGUI(plr, villa, group);
    }

    @Override
    public void update() {
        int id = 0;
        for (int i = 9; i < inv.getSize(); ++i)
            inv.setItem(i, config.getStaticItem("glass"));
        for (String groupName : new TreeSet<>(villa.getGroups().keySet())) {
            ItemStack is = ItemUtils.makeItem(villa.getGroups().get(groupName).getIcon(),
                    StrUtils.fillVariables(conf.getGroupsName(), "name", groupName),
                    conf.getGroupsLore());
            inv.setItem(dataSlots[id], is);
            groupNames.put(dataSlots[id], groupName);
            ++id;
        }
    }
}
