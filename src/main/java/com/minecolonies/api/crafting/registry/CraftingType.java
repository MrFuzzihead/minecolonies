package com.minecolonies.api.crafting.registry;

import com.minecolonies.api.crafting.IGenericRecipe;
import net.minecraft.util.ResourceLocation;
// [1.7.10] RecipeManager not available in 1.7.10
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * Class to represent the different types of crafting supported by MineColonies
 */
public abstract class CraftingType
{
    private ResourceLocation registryName;

    protected CraftingType(@NotNull final ResourceLocation id)
    {
        this.registryName = id;
    }

    /**
     * Find all teachable recipes supported by this particular crafting type
     * @param recipeManager the vanilla recipe manager [1.7.10] stubbed as Object
     * @param world the world (if available)
     * @return the list of teachable recipes
     */
    @NotNull
    public abstract List<IGenericRecipe> findRecipes(@NotNull final Object recipeManager,
                                                     @Nullable final World world);

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof CraftingType)
        {
            return Objects.equals(registryName, ((CraftingType) obj).registryName);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return registryName.hashCode();
    }
}



