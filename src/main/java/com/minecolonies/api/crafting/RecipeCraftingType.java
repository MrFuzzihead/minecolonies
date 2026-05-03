package com.minecolonies.api.crafting;

// [1.7.10 BACKPORT STUB] RecipeType/RecipeManager do not exist in 1.7.10.
import com.minecolonies.api.crafting.registry.CraftingType;
import com.minecolonies.api.util.Log;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * [1.7.10 BACKPORT STUB] A CraftingType for vanilla recipe types.
 * In 1.7.10 there is no RecipeManager; this is a no-op stub.
 */
public class RecipeCraftingType extends CraftingType
{
    public RecipeCraftingType(@NotNull final ResourceLocation id)
    {
        super(id);
    }

    @Override
    @NotNull
    public List<IGenericRecipe> findRecipes(@NotNull final Object recipeManager, @NotNull final World world)
    {
        return Collections.emptyList();
    }
}
