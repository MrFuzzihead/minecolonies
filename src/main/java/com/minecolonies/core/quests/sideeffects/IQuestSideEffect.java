package com.minecolonies.core.quests.sideeffects;
import net.minecraftforge.common.util.INBTSerializable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
// [1.7.10] INBTSerializable -> manual read/write

public interface IQuestSideEffect extends INBTSerializable<NBTTagCompound>
{
    /**
     * Gets the quest effects ID
     *
     * @return res location id
     */
    ResourceLocation getID();

    /**
     * Called on quest start
     */
    default void onStart() {}

    /**
     * Called on quest completion
     */
    default void onFinish() {}

    /**
     * Called on quest cancellation
     */
    default void onCancel() {}

    /**
     * Deserialize the quest side effect.
     * @param nbt the nbt to deserialize it from.
     */
    default void deserializeNBT(final NBTTagCompound nbt)
    {
        // noop
    }

    /**
     * Serialize the side effect to nbt.
     * @return the nbt to serialize it to.
     */
    default NBTTagCompound serializeNBT()
    {
        return new NBTTagCompound();
    }
}



