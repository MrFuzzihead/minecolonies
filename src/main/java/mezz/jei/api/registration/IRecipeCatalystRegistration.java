package mezz.jei.api.registration;

/** [1.7.10 stub] IRecipeCatalystRegistration. */
public interface IRecipeCatalystRegistration
{
    <R> void addRecipeCatalyst(Object ingredient, mezz.jei.api.recipe.RecipeType<R>... types);
}

