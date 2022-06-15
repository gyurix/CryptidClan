package gyurix.cryptidgear.conf;

import gyurix.cryptidcommons.conf.PostProcessable;
import gyurix.cryptidgear.data.Weapon;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.TreeMap;

import static gyurix.cryptidgear.GearManager.weaponCache;

@Getter
public class Config implements PostProcessable {
    TreeMap<String, ItemStack> customItems;
    TreeMap<String, CustomRecipe> recipes;
    TreeMap<String, Weapon> weapons;

    @Override
    public void postProcess() {
        recipes.forEach((name, recipe) -> {
            recipe.setName(name);
            recipe.setOutputItem(customItems.get(name).clone());
            recipe.register(this);
        });
        weapons.forEach((name, weapon) -> weaponCache.put(customItems.get(name).getItemMeta().getDisplayName(), weapon));
    }
}
