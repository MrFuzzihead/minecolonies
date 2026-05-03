package com.minecolonies.api.crafting.registry;

// [1.7.10 BACKPORT STUB] RecipeSerializer/RecipeType do not exist in 1.7.10.
// CompostRecipeSerializer and ZeroWasteRecipeSerializer are lazily initialized during mod setup.
import com.minecolonies.api.crafting.CompostRecipe;
import com.minecolonies.api.crafting.ZeroWasteRecipe;
import com.minecolonies.api.registry.RegistryObject;

/**
 * [1.7.10 BACKPORT STUB] Holds refs to mod recipe serializers and recipe types.
 * In 1.7.10 there is no recipe serializer registry.
 */
public class ModRecipeSerializer
{
    public static RegistryObject<CompostRecipe.Serializer> CompostRecipeSerializer = RegistryObject.of(new CompostRecipe.Serializer());
    public static RegistryObject<ZeroWasteRecipe.Serializer> ZeroWasteRecipeSerializer = RegistryObject.of(ZeroWasteRecipe.Serializer.get());
}
