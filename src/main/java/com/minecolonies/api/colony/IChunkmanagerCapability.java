package com.minecolonies.api.colony;

import com.minecolonies.api.util.ChunkLoadStorage;
import com.minecolonies.api.util.NBTUtils;
// [1.7.10 BACKPORT] Removed:
//   net.minecraft.core.Direction        â€” no capabilities in 1.7.10
//   net.minecraftforge.common.capabilities.Capability â€” no capabilities in 1.7.10
// Capability<> param removed from Storage methods; pass null at call-sites.
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTBase;
import com.minecolonies.api.util.Tuple;
import net.minecraft.world.ChunkCoordIntPair; // [1.7.10] ChunkCoordIntPair ? ChunkCoordIntPair
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import static com.minecolonies.api.util.constant.NbtTagConstants.*;

/**
 * Capability for the colony NBTBase for chunks
 */
public interface IChunkmanagerCapability
{
    /**
     * Get the chunkStorage at a certain location.
     *
     * @param chunkX the x chunk location.
     * @param chunkZ the z chunk location.
     * @return the storage or null.
     */
    @Nullable
    ChunkLoadStorage getChunkStorage(int chunkX, int chunkZ);

    /**
     * Add a new chunkStorage.
     *
     * @param chunkX  chunkX the x chunk location.
     * @param chunkZ  chunkX the z chunk location.
     * @param storage the new to add or update.
     * @return true if override else false.
     */
    boolean addChunkStorage(int chunkX, int chunkZ, ChunkLoadStorage storage);

    /**
     * Get all chunk storages for serialization.
     *
     * @return the storages.
     */
    Map<ChunkCoordIntPair, ChunkLoadStorage> getAllChunkStorages();

    /**
     * The implementation of the colonyTagCapability.
     */
    class Impl implements IChunkmanagerCapability
    {
        /**
         * Map of ChunkCoordIntPair to chunkLoadStorage.
         */
        private final Map<ChunkCoordIntPair, ChunkLoadStorage> chunkStorages = new HashMap<>();

        @Nullable
        @Override
        public ChunkLoadStorage getChunkStorage(final int chunkX, final int chunkZ)
        {
            return chunkStorages.remove(new ChunkCoordIntPair(chunkX, chunkZ));
        }

        @Override
        public boolean addChunkStorage(final int chunkX, final int chunkZ, final ChunkLoadStorage storage)
        {
            final ChunkLoadStorage existingStorage = chunkStorages.get(new ChunkCoordIntPair(chunkX, chunkZ));
            if (existingStorage == null)
            {
                chunkStorages.put(new ChunkCoordIntPair(chunkX, chunkZ), storage);
                return false;
            }
            else
            {
                existingStorage.merge(storage);
                return true;
            }
        }

        @Override
        public Map<ChunkCoordIntPair, ChunkLoadStorage> getAllChunkStorages()
        {
            return chunkStorages;
        }
    }

    /**
     * The storage class of the capability.
     *
     * <p><b>1.7.10 Backport:</b> {@code Capability&lt;&gt;} and {@code Direction} params removed.</p>
     */
    class Storage
    {
        /** @param capability ignored (was Capability&lt;IChunkmanagerCapability&gt;) */
        public static NBTBase writeNBT(@SuppressWarnings("unused") final Object capability,
            @NotNull final IChunkmanagerCapability instance,
            @SuppressWarnings("unused") final Object side)
        {
            final NBTTagCompound compound = new NBTTagCompound();
            compound.put(TAG_ALL_CHUNK_STORAGES,
              instance.getAllChunkStorages().entrySet().stream().map(entry -> write(entry.getKey(), entry.getValue())).collect(NBTUtils.toListNBT()));
            return compound;
        }

        /** @param capability ignored (was Capability&lt;IChunkmanagerCapability&gt;) */
        public static void readNBT(
            @SuppressWarnings("unused") final Object capability,
            @NotNull final IChunkmanagerCapability instance,
            @SuppressWarnings("unused") final Object side,
            @NotNull final NBTBase nbt)
        {
            if (nbt instanceof NBTTagCompound && ((NBTTagCompound) nbt).contains(TAG_ALL_CHUNK_STORAGES))
            {
                NBTUtils.streamCompound(((NBTTagCompound) nbt).getList(TAG_ALL_CHUNK_STORAGES, NBTBase.TAG_COMPOUND))
                  .map(Storage::read).forEach(key -> instance.addChunkStorage(key.getA().x, key.getA().z, key.getB()));
            }
        }

        /**
         * Write a single ChunkCoordIntPair, ChunkLoadStorage pair to nbt.
         *
         * @param key   the key.
         * @param value the value
         * @return the resulting compound.
         */
        private static NBTTagCompound write(final ChunkCoordIntPair key, final ChunkLoadStorage value)
        {
            final NBTTagCompound compound = new NBTTagCompound();
            compound.put(TAG_CHUNK_STORAGE, value.toNBT());
            compound.putInt(TAG_X, key.x);
            compound.putInt(TAG_Z, key.z);
            return compound;
        }

        /**
         * Read a key value pair for the chunkloadstorages.
         *
         * @param compound the compound to read it from.
         * @return a tuple for both.
         */
        private static Tuple<ChunkCoordIntPair, ChunkLoadStorage> read(final NBTTagCompound compound)
        {
            final ChunkLoadStorage storage = new ChunkLoadStorage(compound.getCompound(TAG_CHUNK_STORAGE));
            final int x = compound.getInt(TAG_X);
            final int z = compound.getInt(TAG_Z);
            return new Tuple<>(new ChunkCoordIntPair(x, z), storage);
        }
    }
}




