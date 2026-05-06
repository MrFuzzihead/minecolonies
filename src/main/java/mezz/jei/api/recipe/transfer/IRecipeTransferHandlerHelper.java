package mezz.jei.api.recipe.transfer;

/** [1.7.10 stub] IRecipeTransferHandlerHelper. */
public interface IRecipeTransferHandlerHelper
{
    IRecipeTransferError createInternalError();
    IRecipeTransferError createUserErrorWithTooltip(net.minecraft.util.IChatComponent tooltip);
    IRecipeTransferError createUserErrorForMissingSlots(String message, java.util.Collection<? extends mezz.jei.api.gui.ingredient.IRecipeSlotView> missingSlots);
}
