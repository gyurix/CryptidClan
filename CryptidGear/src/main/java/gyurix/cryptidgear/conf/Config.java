package gyurix.cryptidgear.conf;

import gyurix.cryptidcommons.conf.PostProcessable;
import gyurix.cryptidgear.data.Weapon;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.TreeMap;

@Getter
public class Config implements PostProcessable {
    TreeMap<String, ItemStack> customItems;
    TreeMap<String, CustomRecipe> recipes;
    TreeMap<String, Weapon> weapons;

    @Override
    public void postProcess() {
        recipes.forEach((name, recipe) -> {
            recipe.setOutputItem(customItems.get(name).clone());
            recipe.register();
        });
    }
}
