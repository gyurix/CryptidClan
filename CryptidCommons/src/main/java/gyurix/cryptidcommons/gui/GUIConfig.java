package gyurix.cryptidcommons.gui;

import gyurix.cryptidcommons.conf.PostProcessable;
import gyurix.cryptidcommons.util.ItemUtils;
import gyurix.cryptidcommons.util.StrUtils;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
public class GUIConfig implements PostProcessable {
    private final transient HashMap<Integer, String> customMap = new HashMap<>();
    private final transient HashMap<Integer, ItemStack> staticMap = new HashMap<>();
    private HashMap<String, ItemStack> customItems;
    private List<String> layout;
    private HashMap<String, ItemStack> staticItems;
    private String title;

    public ItemStack getCustomItem(String key, Object... vars) {
        return ItemUtils.fillVariables(customItems.get(key), vars);
    }

    public ItemStack getStaticItem(String key) {
        return ItemUtils.fillVariables(staticItems.get(key));
    }

    @Override
    public void postProcess() {
        title = StrUtils.colorize(title);
        int slot = 0;
        List<String> fixedLayout = new ArrayList<>();
        for (String row : layout) {
            String[] cols = row.split(" +");
            if (cols.length != 9)
                throw new RuntimeException("Invalid gui layout, 9 columns excepted, found " + cols.length + " in row " + row);
            for (String col : cols) {
                fixedLayout.add(col);
                if (staticItems.containsKey(col))
                    staticMap.put(slot, staticItems.get(col));
                else
                    customMap.put(slot, col);
                ++slot;
            }
        }
        layout = fixedLayout;
    }
}
