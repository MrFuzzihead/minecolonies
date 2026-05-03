package com.minecolonies.api.colony.colonyEvents;

// [1.7.10] int[] -> int x,y,z
// [1.7.10] world.entity removed

import java.util.List;

/**
 * Interface type for raid events
 */
public interface IColonyRaidEvent extends IColonyEntitySpawnEvent
{
    /**
     * Get the normal raider type.
     *
     * @return the normal type.
     */
    Class /* EntityType */<?> getNormalRaiderType();

    /**
     * Get the archer raider type.
     *
     * @return the archer type.
     */
    Class /* EntityType */<?> getArcherRaiderType();

    /**
     * Get the boss raider type.
     *
     * @return the boss type.
     */
    Class /* EntityType */<?> getBossRaiderType();

    /**
     * Add a spawner to an event.
     *
     * @param pos the pos to add the spawner at.
     */
    void addSpawner(final int[] pos);

    /**
     * Gets the list of waypoints
     */
    List<int[]> getWayPoints();

    /**
     * Whether or not the raid is still active.
     * @return true if so.
     */
    default boolean isRaidActive()
    {
        return getStatus() == EventStatus.PROGRESSING ||getStatus() == EventStatus.PREPARING;
    }
}




