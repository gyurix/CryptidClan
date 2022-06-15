package gyurix.cryptidgear.conf;

import gyurix.cryptidcommons.util.ItemUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.HashMap;
import java.util.List;

@Getter
public class CustomRecipe {
    private final transient HashMap<Integer, ItemStack> ingredientMap = new HashMap<>();
    private HashMap<String, String> ingredients;
    @Setter
    private String name;
    @Setter
    private ItemStack outputItem;
    private List<String> shape;

    public void register(Config conf) {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey("gear", name.toLowerCase()), getOutputItem());
        recipe.shape(shape.toArray(new String[0]));
        ingredients.forEach((name, value) -> {
            ItemStack ingredient = conf.getCustomItems().computeIfAbsent(value, ItemUtils::stringToItemStack);
            recipe.setIngredient(name.charAt(0), ingredient);
        });
        Bukkit.addRecipe(recipe);
    }

    public void unregister() {
        Bukkit.removeRecipe(new NamespacedKey("gear", name.toLowerCase()));
    }
}
