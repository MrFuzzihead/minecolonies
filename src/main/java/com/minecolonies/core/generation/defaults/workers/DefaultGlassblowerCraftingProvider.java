package com.minecolonies.core.generation.defaults.workers;

import com.minecolonies.api.colony.jobs.ModJobs;
import com.minecolonies.core.generation.CustomRecipeProvider;
// [1.7.10] data removed
// [1.7.10] data removed
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Datagen for Glassblower
 */
public class DefaultGlassblowerCraftingProvider extends CustomRecipeProvider
{
    private static final String GLASSBLOWER = ModJobs.GLASSBLOWER_ID.getPath();

    public DefaultGlassblowerCraftingProvider(@NotNull final PackOutput packOutput)
    {
        super(packOutput);
    }

    @NotNull
    @Override
    public String getName()
    {
        return "DefaultGlassblowerCraftingProvider";
    }

    @Override
    protected void registerRecipes(@NotNull final Consumer<FinishedRecipe> consumer)
    {
    }
}

