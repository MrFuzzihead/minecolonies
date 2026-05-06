package mezz.jei.api.gui.ingredient;

/** [1.7.10 stub] IRecipeSlotsView. */
public interface IRecipeSlotsView
{
    java.util.List<IRecipeSlotView> getSlotViews(mezz.jei.api.recipe.RecipeIngredientRole role);
    java.util.List<IRecipeSlotView> getSlotViews();
}
