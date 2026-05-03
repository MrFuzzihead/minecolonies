package com.minecolonies.api.entity.citizen.happiness;

import com.minecolonies.api.colony.ICitizenData;
import net.minecraft.nbt.NBTTagCompound;
// [1.7.10] INBTSerializable does not exist; use manual serialize/deserialize pattern.

/**
 * Wrapper to deal with happiness suppliers.
 */
public interface IHappinessSupplierWrapper
{
    /**
     * Get the matching value.
     * @param citizenData the context.
     * @return the value.
     */
    double getValue(final ICitizenData citizenData);

    /**
     * Get the last cache value in absence of the citizen.
     * @return the last cached value.
     */
    double getLastCachedValue();

    /**
     * Serialize to NBT.
     * @return the compound.
     */
    NBTTagCompound serializeNBT();

    /**
     * Deserialize from NBT.
     * @param nbt the compound.
     */
    void deserializeNBT(NBTTagCompound nbt);
}
