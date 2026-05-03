package com.minecolonies.core.colony;

// [1.7.10 BACKPORT] Replacement for the IChunkmanagerCapability + world capability provider.
//
// In 1.21, the pending-chunk-load data was stored as a Forge capability attached to World:
//   world.getCapability(CHUNK_STORAGE_UPDATE_CAP, null).resolve().orElse(null)
//
// In 1.7.10, this is replaced by a WorldSavedData stored in MapStorage.
//
// Usage:
//   IChunkmanagerCapability cap = ChunkManagerWorldSavedData.getOrCreate(world).getCapability();

import com.minecolonies.api.colony.IChunkmanagerCapability;
import com.minecolonies.api.util.Log;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * WorldSavedData wrapper around {@link IChunkmanagerCapability}.
 *
 * <p>Replaces the 1.21 Forge capability system for world-World pending-chunk-load storage.</p>
 */
public class ChunkManagerWorldSavedData extends WorldSavedData
{
    public static final String SAVE_ID = "minecolonies_chunk_manager";

    @NotNull
    private final IChunkmanagerCapability capability = new IChunkmanagerCapability.Impl();

    public ChunkManagerWorldSavedData()
    {
        super(SAVE_ID);
    }

    public ChunkManagerWorldSavedData(final String id)
    {
        super(id);
    }

    /**
     * Returns (or lazily creates) the {@link ChunkManagerWorldSavedData} for the given world.
     *
     * <p>Replaces all former
     * {@code world.getCapability(CHUNK_STORAGE_UPDATE_CAP, null).resolve().orElse(null)}
     * call-sites.</p>
     */
    @NotNull
    public static ChunkManagerWorldSavedData getOrCreate(@NotNull final World world)
    {
        ChunkManagerWorldSavedData data =
            (ChunkManagerWorldSavedData) world.mapStorage.loadData(ChunkManagerWorldSavedData.class, SAVE_ID);
        if (data == null)
        {
            data = new ChunkManagerWorldSavedData();
            world.mapStorage.setData(SAVE_ID, data);
        }
        return data;
    }

    @Nullable
    public static ChunkManagerWorldSavedData getIfLoaded(@NotNull final World world)
    {
        return (ChunkManagerWorldSavedData) world.mapStorage.loadData(ChunkManagerWorldSavedData.class, SAVE_ID);
    }

    @NotNull
    public IChunkmanagerCapability getCapability()
    {
        return capability;
    }

    @Override
    public void readFromNBT(@NotNull final NBTTagCompound compound)
    {
        try
        {
            IChunkmanagerCapability.Storage.readNBT(null, capability, null, compound);
        }
        catch (final Exception e)
        {
            Log.getLogger().error("ChunkManagerWorldSavedData: error reading NBT", e);
        }
    }

    @Override
    public void writeToNBT(@NotNull final NBTTagCompound compound)
    {
        try
        {
            final NBTTagCompound written = (NBTTagCompound) IChunkmanagerCapability.Storage.writeNBT(null, capability, null);
            for (final Object key : written.func_150296_c())
            {
                compound.setTag((String) key, written.getTag((String) key));
            }
        }
        catch (final Exception e)
        {
            Log.getLogger().error("ChunkManagerWorldSavedData: error writing NBT", e);
        }
    }
}


