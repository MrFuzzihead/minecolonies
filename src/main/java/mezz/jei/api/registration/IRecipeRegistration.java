package mezz.jei.api.registration;

import mezz.jei.api.helpers.IJeiHelpers;

/** [1.7.10 stub] IRecipeRegistration. */
public interface IRecipeRegistration
{
    IJeiHelpers getJeiHelpers();
    <T> void addRecipes(mezz.jei.api.recipe.RecipeType<T> recipeType, java.util.List<? extends T> recipes);
    void addRecipeCatalyst(net.minecraft.item.ItemStack stack, mezz.jei.api.recipe.RecipeType<?>... recipeTypes);
    void addIngredientInfo(net.minecraft.item.ItemStack stack, Object ingredientType, String... descriptionKeys);
    void addIngredientInfo(java.util.List<net.minecraft.item.ItemStack> stacks, Object ingredientType, String... descriptionKeys);
}

