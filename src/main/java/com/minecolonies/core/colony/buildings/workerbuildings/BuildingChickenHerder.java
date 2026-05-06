package com.minecolonies.core.colony.buildings.workerbuildings;
import net.minecraft.world.entity.animal.Animal;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.jobs.ModJobs;
import com.minecolonies.api.crafting.GenericRecipe;
import com.minecolonies.api.crafting.IGenericRecipe;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.core.colony.buildings.AbstractBuilding;
import com.minecolonies.core.colony.buildings.modules.AnimalHerdingModule;
// [1.7.10] int[] -> int x,y,z
// [1.7.10] world.entity removed
// [1.7.10] world.entity removed
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates a new building for the Chicken Herder.
 */
public class BuildingChickenHerder extends AbstractBuilding
{
    /**
     * Description of the job executed in the hut.
     */
    private static final String JOB = "chickenherder";

    /**
     * The hut name, used for the lang string in the GUI
     */
    private static final String HUT_NAME = "chickenherderhut";

    /**
     * Max building World of the hut.
     */
    private static final int MAX_BUILDING_LEVEL = 5;

    /**
     * Instantiates the building.
     *
     * @param c the colony.
     * @param l the location.
     */
    public BuildingChickenHerder(final IColony c, final int[] l)
    {
        super(c, l);
    }

    @NotNull
    @Override
    public String getSchematicName()
    {
        return JOB;
    }

    /**
     * Chicken herding module
     */
    public static class HerdingModule extends AnimalHerdingModule
    {
        public HerdingModule()
        {
            super(ModJobs.chickenHerder.get(), a -> a instanceof net.minecraft.entity.passive.EntityChicken, new ItemStorage(Items.wheat_seeds, 2));
        }

        @NotNull
        @Override
        public List<IGenericRecipe> getRecipesForDisplayPurposesOnly(@NotNull net.minecraft.entity.passive.EntityAnimal animal)
        {
            // [1.7.10] EntityType not available; return parent result only
            return new ArrayList<>(super.getRecipesForDisplayPurposesOnly(animal));
        }
    }
}



