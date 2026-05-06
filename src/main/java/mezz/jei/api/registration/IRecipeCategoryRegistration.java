package mezz.jei.api.registration;

import mezz.jei.api.helpers.IJeiHelpers;

/** [1.7.10 stub] IRecipeCategoryRegistration. */
public interface IRecipeCategoryRegistration
{
    IJeiHelpers getJeiHelpers();
    void addRecipeCategories(mezz.jei.api.recipe.category.IRecipeCategory<?>... categories);
}

