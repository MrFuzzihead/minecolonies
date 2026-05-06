package mezz.jei.api.recipe.transfer;

/** [1.7.10 stub] IRecipeTransferHandler. */
public interface IRecipeTransferHandler<C extends net.minecraft.inventory.Container, R>
{
    Class<? extends C> getContainerClass();
    java.util.Optional<mezz.jei.api.recipe.RecipeType<R>> getRecipeType();
    @org.jetbrains.annotations.Nullable
    IRecipeTransferError transferRecipe(C container, R recipe, mezz.jei.api.gui.ingredient.IRecipeSlotsView recipeSlotsView, net.minecraft.entity.player.EntityPlayer player, boolean maxTransfer, boolean doTransfer);
}

