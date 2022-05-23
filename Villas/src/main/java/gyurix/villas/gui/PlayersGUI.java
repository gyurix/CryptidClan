package gyurix.villas.gui;

import gyurix.cryptidcommons.gui.CustomGUI;
import gyurix.cryptidcommons.util.ChatDataReader;
import gyurix.villas.data.Group;
import gyurix.villas.data.Villa;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

import static gyurix.cryptidcommons.util.ItemUtils.makeSkull;
import static gyurix.cryptidcommons.util.StrUtils.fillVariables;
import static gyurix.villas.conf.ConfigManager.conf;
import static gyurix.villas.conf.ConfigManager.msg;

public class PlayersGUI extends CustomGUI {
    private final HashMap<Integer, String> playerNames = new HashMap<>();
    private final Villa villa;

    public PlayersGUI(Player plr, Villa villa) {
        super(plr, conf.getGuis().get("players"));
        this.villa = villa;

        String title = fillVariables(config.getTitle(), "villa", villa.getName());
        int rows = (villa.getPlayers().size() + 8) / 9;
        inv = Bukkit.createInventory(this, config.getLayout().size() + rows * 9, title);

        config.getStaticMap().forEach((slot, is) -> inv.setItem(slot, is));
        update();
        plr.openInventory(inv);
    }

    @Override
    public void onClick(int slot, boolean right, boolean shift) {
        if (slot >= inv.getSize() || slot < 0) return;
        String type = slot < config.getLayout().size() ? config.getLayout().get(slot) : "";
        switch (type) {
            case "add" -> {
                if (!villa.hasPermission(plr, Group::isManage)) {
                    msg.msg(plr, "noperm.manage");
                    return;
                }
                plr.closeInventory();
                msg.msg(plr, "player.enter");
                new ChatDataReader(plr, (pln) -> {
                    villa.addPlayer(plr, pln);
                    new PlayersGUI(plr, villa);
                }, () -> {
                    msg.msg(plr, "player.cancel");
                    new PlayersGUI(plr, villa);
                });
            }
            case "back" -> {
                new ManageGUI(plr, villa);
                return;
            }
            case "exit" -> {
                plr.closeInventory();
                return;
            }
        }
        String pln = playerNames.get(slot);
        if (pln == null)
            return;
        if (!villa.getPlayerNames().contains(pln)) {
            new PlayersGUI(plr, villa);
            return;
        }
        if (!villa.hasPermission(plr, Group::isManage)) {
            msg.msg(plr, "noperm.manage");
            return;
        }
        if (right) {
            if (villa.kick(plr, pln))
                new PlayersGUI(plr,villa);
        } else
            new PlayerGroupSelectorGUI(plr, villa, pln);
    }

    @Override
    public void update() {
        int id = 0;
        for (int i = 9; i < inv.getSize(); ++i)
            inv.setItem(i, config.getStaticItem("glass"));
        for (String pln : villa.getPlayerNames()) {
            Object[] vars = new Object[]{
                    "name", pln,
                    "group", villa.getPlayers().get(Bukkit.getPlayerUniqueId(pln))
            };
            ItemStack is = makeSkull(pln, fillVariables(conf.getPlayersName(), vars), fillVariables(conf.getPlayersLore(), vars));
            inv.setItem(dataSlots[id], is);
            playerNames.put(dataSlots[id], pln);
            ++id;
        }
    }
}
