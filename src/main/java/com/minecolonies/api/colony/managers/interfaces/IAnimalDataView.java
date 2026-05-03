package com.minecolonies.api.colony.managers.interfaces;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// [1.7.10] int[] -> int x,y,z
import net.minecraft.network.PacketBuffer;

public interface IAnimalDataView 
{
    /**
     * Deserialize the attributes and variables from transition.
     *
     * @param buf Byte buffer to deserialize.
     */
    void deserialize(@NotNull PacketBuffer buf);

    /**
     * Get the id of the animal.
     *
     * @return the animal id.
     */
    int getId();

    /**
     * Gets the block position of the animal's home building.
     * 
     * @return the block position of the home building, or null if the animal does not have a home building.
     */
    @Nullable
    public int[] getHomeBuilding();

    /**
     * Gets the combat cooldown of the animal.
     * 
     * @return the combat cooldown of the animal.
     */
    public float getCombatCooldown();

}


