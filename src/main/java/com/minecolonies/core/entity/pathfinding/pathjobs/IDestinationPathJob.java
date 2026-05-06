package com.minecolonies.core.entity.pathfinding.pathjobs;
import net.minecraft.util.Direction;

// [1.7.10] int[] -> int x,y,z

/**
 * Interface for path jobs with a destination/desired direction
 */
public interface IDestinationPathJob
{
    /**
     * Return the destination
     *
     * @return destination
     */
    public int[] getDestination();
}


