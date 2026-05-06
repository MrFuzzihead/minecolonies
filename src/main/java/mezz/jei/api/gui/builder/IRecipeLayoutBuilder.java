package mezz.jei.api.gui.builder;

/** [1.7.10 stub] IRecipeLayoutBuilder. */
public interface IRecipeLayoutBuilder
{
    IRecipeSlotBuilder addSlot(mezz.jei.api.recipe.RecipeIngredientRole role, int x, int y);
    IRecipeSlotBuilder addInputSlot(int x, int y);
    IRecipeSlotBuilder addOutputSlot(int x, int y);
    void moveRecipeTransferButton(int posX, int posY);
}
