package com.minecolonies.core.generation.defaults.workers;
import net.minecraft.world.item.crafting.FinishedRecipe;
import net.minecraft.data.PackOutput;

import com.minecolonies.api.colony.jobs.ModJobs;
import com.minecolonies.core.generation.CustomRecipeProvider;
// [1.7.10] data removed
// [1.7.10] data removed
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Datagen for StoneSmeltery
 */
public class DefaultStoneSmelteryCraftingProvider extends CustomRecipeProvider
{
    private static final String STONE_SMELTERY = ModJobs.STONE_SMELTERY_ID.getPath();

    public DefaultStoneSmelteryCraftingProvider(@NotNull final PackOutput packOutput)
    {
        super(packOutput);
    }

    @NotNull
    @Override
    public String getName()
    {
        return "DefaultStoneSmelteryCraftingProvider";
    }

    @Override
    protected void registerRecipes(@NotNull final Consumer<FinishedRecipe> consumer)
    {
    }
}

