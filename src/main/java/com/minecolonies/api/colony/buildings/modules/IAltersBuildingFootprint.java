package com.minecolonies.api.colony.buildings.modules;
import net.minecraft.util.Direction;

// [1.7.10] int[] -> int x,y,z
import com.minecolonies.api.util.Tuple;

/**
 * Interface for buildings with an extended footprint.
 */
public interface IAltersBuildingFootprint extends IAssignsCitizen
{
    /**
     * Get the additional corners into each direction.
     * @return the positions.
     */
    Tuple<int[], int[]> getAdditionalCorners();
}


