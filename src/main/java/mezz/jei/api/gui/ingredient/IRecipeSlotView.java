package mezz.jei.api.gui.ingredient;

/** [1.7.10 stub] IRecipeSlotView. */
public interface IRecipeSlotView
{
    java.util.stream.Stream<net.minecraft.item.ItemStack> getItemStacks();
    java.util.stream.Stream<?> getAllIngredients();
    <V> java.util.Optional<V> getDisplayedIngredient(Object type);
    mezz.jei.api.recipe.RecipeIngredientRole getRole();
}
