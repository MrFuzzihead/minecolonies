package com.minecolonies.api.blocks.interfaces;

// [1.7.10] forge event removed
import org.jetbrains.annotations.NotNull;

/**
 * Right-clicking this block in the air triggers the building browser window interface.
 */
public interface IBuildingBrowsableBlock
{
    /**
     * Return false if you want to prevent the building search behaviour for some reason.  Client-side only.
     */
    default boolean shouldBrowseBuildings(@NotNull final Object event)
    {
        return true;
    }
}

