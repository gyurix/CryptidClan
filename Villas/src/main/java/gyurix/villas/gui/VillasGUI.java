package gyurix.villas.gui;

import gyurix.cryptidcommons.gui.CustomGUI;
import gyurix.cryptidcommons.util.ItemUtils;
import gyurix.villas.VillaManager;
import gyurix.villas.data.Group;
import gyurix.villas.data.Villa;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static gyurix.villas.conf.ConfigManager.conf;
import static gyurix.villas.conf.ConfigManager.msg;
import static java.util.Comparator.comparing;

public class VillasGUI extends CustomGUI {
    private final List<Villa> villas = new ArrayList<>();

    private boolean buyable;
    private boolean orderByPrice;
    private boolean desc;
    private int page = 1, maxPage;

    public VillasGUI(Player plr) {
        super(plr, conf.getGuis().get("villas"));
        calculateVillas();
        open();
    }

    private void calculateVillas() {
        villas.clear();
        for (Villa villa : VillaManager.villas.values()) {
            if (villa.hasPermission(plr, Group::isSee) && (villa.isBuyable() || !buyable))
                villas.add(villa);
        }
        Comparator<Villa> comp = orderByPrice ? comparing(Villa::getPrice) : comparing(Villa::getName);
        if (desc)
            comp = comp.reversed();
        villas.sort(comp);
        maxPage = Math.max(1, (villas.size() + 35) / 36);
        page = Math.min(maxPage, page);
    }

    @Override
    public ItemStack getCustomItem(String name) {
        HashMap<String, ItemStack> items = config.getCustomItems();
        ItemStack glass = config.getStaticItem("glass");
        switch (name) {
            case "buyable" -> {
                return items.get("buyable" + (buyable ? "On" : "Off"));
            }
            case "orderBy" -> {
                return items.get("order" + (orderByPrice ? "Price" : "Name"));
            }
            case "order" -> {
                return items.get("order" + (desc ? "Desc" : "Asc"));
            }
            case "prev" -> {
                return page > 1 ? items.get("prev") : glass;
            }
            case "page" -> {
                return ItemUtils.fillVariables(items.get("page"), "page", page, "maxPage", maxPage);
            }
            case "next" -> {
                return page < maxPage ? items.get("next") : glass;
            }
        }
        throw new RuntimeException("Unknown custom item " + name);
    }

    @Override
    public void onClick(int slot, boolean right, boolean shift) {
        if (slot >= inv.getSize() || slot < 0) return;
        if (slot > 8 && slot < 45) {
            int id = (page - 1) * 36 + slot - 9;
            if (id >= villas.size())
                return;
            villaClick(villas.get(id), right, shift);
            return;
        }
        String type = config.getLayout().get(slot);
        switch (type) {
            case "exit" -> {
                plr.closeInventory();
            }
            case "buyable" -> {
                buyable = !buyable;
                calculateVillas();
            }
            case "orderBy" -> {
                orderByPrice = !orderByPrice;
                calculateVillas();
                update();
            }
            case "order" -> {
                desc = !desc;
                calculateVillas();
                update();
            }
            case "prev" -> {
                if (page > 1) {
                    --page;
                    update();
                }
            }
            case "next" -> {
                if (page < maxPage) {
                    ++page;
                    update();
                }
            }
        }
    }

    private void villaClick(Villa villa, boolean right, boolean shift) {
        if (!villa.hasPermission(plr, Group::isSee)) {
            calculateVillas();
            update();
            return;
        }
        if (right) {
            if (!buyable)
                return;
            villa.buy(plr);
        } else if (shift) {
            if (!villa.hasPermission(plr, Group::isInfo)) {
                msg.msg(plr, "noperm.info");
                return;
            }
            new ManageGUI(plr, villa);
        } else {
            if (!villa.hasPermission(plr, Group::isTp)) {
                msg.msg(plr, "noperm.tp");
                return;
            }
            msg.msg(plr, "tp", "villa", villa.getName());
            plr.teleport(villa.getSpawn().toLocation());
        }
    }

    @Override
    public void update() {

    }
}
