package com.minecolonies.core.generation.defaults.workers;

import com.minecolonies.api.colony.jobs.ModJobs;
import com.minecolonies.core.generation.CustomRecipeProvider;
// [1.7.10] data removed
// [1.7.10] data removed
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Datagen for Planter
 */
public class DefaultPlanterCraftingProvider extends CustomRecipeProvider
{
    private static final String PLANTER = ModJobs.PLANTER_ID.getPath();

    public DefaultPlanterCraftingProvider(@NotNull final PackOutput packOutput)
    {
        super(packOutput);
    }

    @NotNull
    @Override
    public String getName()
    {
        return "DefaultPlanterCraftingProvider";
    }

    @Override
    protected void registerRecipes(@NotNull final Consumer<FinishedRecipe> consumer)
    {
    }
}

