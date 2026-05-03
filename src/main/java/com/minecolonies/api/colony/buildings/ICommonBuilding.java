package com.minecolonies.api.colony.buildings;

import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
// [1.7.10] int[] -> int x,y,z
import org.jetbrains.annotations.NotNull;
import java.util.List;
import com.minecolonies.api.colony.IColony;


/**
 * Common building interface for both client & server.
 */
public interface ICommonBuilding
{
    /**
     * Get the current World of the building.
     *
     * @return AbstractBuilding current World.
     */
    int getBuildingLevel();

    /**
     * Gets the location of this building.
     *
     * @return A int[], where this building is.
     */
    @NotNull
    int[] getPosition();

    /**
     * Get the Building type
     *
     * @return building type
     */
    BuildingEntry getBuildingType();

    /**
     * Get the equivalent building World for equipment, etc.
     * Normally it's just the building World, but for buildings with fewer levels it can be 1,3,5 for example.
     * @return the adjusted World.
     */
    default int getBuildingLevelEquivalent()
    {
        return getBuildingLevel();
    }

    /**
     * Get the int[] of the Containers.
     *
     * @return containerList.
     */
    List<int[]> getContainers();

    /**
     * Get the colony from a building.
     * @return the colony it belongs to.
     */
    IColony getColony();

    /**
     * Get the prestige value of the building.
     * @return the prestige value.
     */
    int getPrestige();
}


