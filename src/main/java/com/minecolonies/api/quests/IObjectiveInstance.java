package com.minecolonies.api.quests;

import net.minecraft.nbt.NBTTagCompound;
// [1.7.10] INBTSerializable -> manual read/write

/**
 * Objective data type to take track of activities.
 */
public interface IObjectiveInstance
{
    // [1.7.10] INBTSerializable replaced by explicit methods
    void readFromNBT(NBTTagCompound compound);
    NBTTagCompound writeToNBT(NBTTagCompound compound);

    /**
     * Check if the objective has been fulfilled.
     * @return true if so.
     */
    boolean isFulfilled();

    /**
     * Get the missing quantity.
     * @return the quantity.
     */
    int getMissingQuantity();
}



