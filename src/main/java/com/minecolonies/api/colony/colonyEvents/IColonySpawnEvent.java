package com.minecolonies.api.colony.colonyEvents;

// [1.7.10] int[] -> int x,y,z

/**
 * An colony event which spawns at a certain position
 */
public interface IColonySpawnEvent extends IColonyEvent
{
    /**
     * Sets the spawn point
     *
     * @param spawnPoint the spawn point to set.
     */
    void setSpawnPoint(int[] spawnPoint);

    /**
     * The position the event starts at
     *
     * @return the spawn pos.
     */
    int[] getSpawnPos();
}


