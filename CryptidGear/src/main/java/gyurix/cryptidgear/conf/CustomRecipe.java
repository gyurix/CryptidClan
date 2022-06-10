package gyurix.cryptidgear.conf;

import gyurix.cryptidcommons.util.ItemUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.HashMap;
import java.util.List;

import static gyurix.cryptidgear.conf.ConfigManager.conf;

@Getter
public class CustomRecipe {
    private final transient HashMap<Integer, ItemStack> ingredientMap = new HashMap<>();
    private HashMap<String, String> ingredients;
    @Setter
    private ItemStack outputItem;
    private List<String> shape;

    public void register() {
        ShapedRecipe recipe = new ShapedRecipe(getOutputItem());
        ingredients.forEach((name, value) -> {
            ItemStack ingredient = conf.getCustomItems().computeIfAbsent(value, ItemUtils::stringToItemStack);
            recipe.setIngredient(name.charAt(0), ingredient);
        });
        recipe.shape(shape.toArray(new String[0]));
        Bukkit.addRecipe(recipe);
    }

}
