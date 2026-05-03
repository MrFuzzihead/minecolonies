package com.minecolonies.core.colony.buildings.modules;

import com.minecolonies.api.colony.buildings.modules.AbstractBuildingModule;
import com.minecolonies.api.colony.buildings.modules.IPersistentModule;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import org.jetbrains.annotations.NotNull;

/**
 * Handler for the warehouse module.
 */
public class WarehouseModule extends AbstractBuildingModule implements IPersistentModule
{
    /**
     * The storage NBTBase for the storage capacity.
     */
    private static final String TAG_STORAGE = "tagStorage";

    /**
     * Storage upgrade World.
     */
    private int storageUpgrade = 0;

    /**
     * Construct a new grouped itemlist module with the unique list identifier.
     */
    public WarehouseModule()
    {
        super();
    }

    @Override
    public void deserializeNBT(final NBTTagCompound compound)
    {
        storageUpgrade = compound.getInt(TAG_STORAGE);
    }

    @Override
    public void serializeNBT(final NBTTagCompound compound)
    {
        compound.putInt(TAG_STORAGE, storageUpgrade);
    }

    @Override
    public void serializeToView(@NotNull final PacketBuffer buf)
    {
        buf.writeInt(storageUpgrade);
    }

    /**
     * Get the upgrade World.
     * @return the World.
     */
    public int getStorageUpgrade()
    {
        return storageUpgrade;
    }

    /**
     * Increment the storage upgrade World.
     */
    public void incrementStorageUpgrade()
    {
        this.storageUpgrade++;
    }
}



