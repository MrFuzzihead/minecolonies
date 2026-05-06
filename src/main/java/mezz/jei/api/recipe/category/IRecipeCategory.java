package mezz.jei.api.recipe.category;

import net.minecraft.util.ResourceLocation;

/** [1.7.10 stub] IRecipeCategory. */
public interface IRecipeCategory<T>
{
    mezz.jei.api.recipe.RecipeType<T> getRecipeType();
    net.minecraft.util.IChatComponent getTitle();
    mezz.jei.api.gui.drawable.IDrawable getBackground();
    mezz.jei.api.gui.drawable.IDrawable getIcon();
    void setRecipe(mezz.jei.api.gui.builder.IRecipeLayoutBuilder builder, T recipe, mezz.jei.api.recipe.IFocusGroup focuses);
    void draw(T recipe, mezz.jei.api.gui.ingredient.IRecipeSlotsView recipeSlotsView, net.minecraft.client.gui.GuiGraphics stack, double mouseX, double mouseY);
}
