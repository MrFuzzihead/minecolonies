package com.minecolonies.api.colony.requestsystem.location;

// [1.7.10] int[] -> int x,y,z
// [1.7.10] int /* ResourceKey */ -> int dimensionId
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * Interface used to describe locations in the world.
 */
public interface ILocation
{

    /**
     * Method to get the location in the dimension
     *
     * @return The location.
     */
    @NotNull
    int[] getInDimensionLocation();

    /**
     * Method to get the dimension of the location.
     *
     * @return The dimension of the location.
     */
    @NotNull
    int /* ResourceKey */ getDimension();

    /**
     * Method to check if this location is reachable from the other.
     *
     * @param location The check if it is reachable from here.
     * @return True when reachable, false when not.
     */
    boolean isReachableFromLocation(@NotNull ILocation location);
}


