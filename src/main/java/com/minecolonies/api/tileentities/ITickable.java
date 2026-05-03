package com.minecolonies.api.tileentities;

/**
 * Interface for tickable things.
 * In 1.7.10, TileEntities tick via updateEntity() — implementations call tick() from there.
 */
public interface ITickable
{
    /**
     * Default parameterless ticking implementation.
     */
    default void tick()
    {
    }
}
