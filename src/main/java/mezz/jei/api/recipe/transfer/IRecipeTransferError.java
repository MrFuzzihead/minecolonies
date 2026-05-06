package mezz.jei.api.recipe.transfer;

/** [1.7.10 stub] IRecipeTransferError. */
public interface IRecipeTransferError
{
    Type getType();

    enum Type { INTERNAL, USER_FACING }
}

