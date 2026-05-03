package com.minecolonies.api.inventory.api;

// [1.7.10] broken import removed
import org.jetbrains.annotations.Nullable;

/**
 * Created by marcf on 3/25/2017.
 */
public interface IWorldNameableModifiable extends net.minecraft.world.IWorldNameable
{
    /**
     * Method to set the name of this {@link net.minecraft.world.IWorldNameable}.
     *
     * @param name The new name of this {@link net.minecraft.world.IWorldNameable}, or null to reset it to its default.
     */
    void setName(@Nullable String name);
}


