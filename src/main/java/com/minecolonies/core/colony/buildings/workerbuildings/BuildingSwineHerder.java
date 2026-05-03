package com.minecolonies.core.colony.buildings.workerbuildings;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.core.colony.buildings.AbstractBuilding;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

/**
 * Creates a new building for the Swine Herder.
 */
public class BuildingSwineHerder extends AbstractBuilding
{
    /**
     * Description of the job executed in the hut.
     */
    private static final String JOB = "swineherder";

    /**
     * Description of the block used to set this block.
     */
    private static final String HUT_NAME = "swineherderhut";

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
    public BuildingSwineHerder(final IColony c, final int[] l)
    {
        super(c, l);
    }

    @NotNull
    @Override
    public String getSchematicName()
    {
        return JOB;
    }

    @Override
    public int getMaxBuildingLevel()
    {
        return MAX_BUILDING_LEVEL;
    }

    @Override
    public boolean canEat(final ItemStack stack)
    {
        if (stack.getItem() == Items.CARROT)
        {
            return false;
        }
        return super.canEat(stack);
    }
}


