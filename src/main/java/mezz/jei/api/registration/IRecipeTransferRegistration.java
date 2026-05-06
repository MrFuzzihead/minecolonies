package mezz.jei.api.registration;

/** [1.7.10 stub] IRecipeTransferRegistration. */
public interface IRecipeTransferRegistration
{
    mezz.jei.api.helpers.IJeiHelpers getJeiHelpers();
    IRecipeTransferHandlerHelper getTransferHelper();
    <C extends net.minecraft.inventory.Container, R> void addRecipeTransferHandler(
        mezz.jei.api.recipe.transfer.IRecipeTransferHandler<C, R> handler,
        mezz.jei.api.recipe.RecipeType<R> recipeType);
    <C extends net.minecraft.inventory.Container, R> void addRecipeTransferHandler(
        mezz.jei.api.recipe.transfer.IRecipeTransferHandler<C, R> handler);
}
