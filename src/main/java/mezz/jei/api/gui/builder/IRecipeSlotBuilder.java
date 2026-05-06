package mezz.jei.api.gui.builder;

/** [1.7.10 stub] IRecipeSlotBuilder. */
public interface IRecipeSlotBuilder
{
    IRecipeSlotBuilder addIngredients(mezz.jei.api.recipe.RecipeIngredientRole role, java.util.List<?> ingredients);
    IRecipeSlotBuilder addItemStacks(java.util.List<net.minecraft.item.ItemStack> stacks);
    IRecipeSlotBuilder addItemStack(net.minecraft.item.ItemStack stack);
    IRecipeSlotBuilder setBackground(mezz.jei.api.gui.drawable.IDrawable drawable, int xOffset, int yOffset);
    IRecipeSlotBuilder addTooltipCallback(mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback callback);
    IRecipeSlotBuilder setOverlay(mezz.jei.api.gui.drawable.IDrawable drawable, int xOffset, int yOffset);
}
