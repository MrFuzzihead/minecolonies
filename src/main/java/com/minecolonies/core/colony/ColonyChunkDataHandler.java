package com.minecolonies.core.colony;

// [1.7.10 BACKPORT] Replacement for MinecoloniesChunkCapabilityProvider + IColonyTagCapability
// chunk capability attachment.
//
// In 1.21, per-chunk colony data was stored as a Forge capability attached to LevelChunk:
//   chunk.getCapability(CLOSE_COLONY_CAP, null).resolve().orElse(null)
//
// In 1.7.10, ChunkAPI (com.falsepattern:chunkapi-mc1.7.10:0.8.2) provides an
// IChunkDataHandler that reads/writes extra NBT data for each chunk. We use this to store
// and retrieve the IColonyTagCapability data.
//
// The per-chunk IColonyTagCapability.Impl instances are kept in a static WeakHashMap keyed
// by the Chunk object so that existing colony logic (ColonyUtils.getOwningColony, etc.) can
// access them without changing every call-site.
//
// Usage:
//   IColonyTagCapability cap = ColonyChunkDataHandler.getColonyTagCapability(chunk);

import com.falsepattern.chunk.api.ArrayUtil;
import com.falsepattern.chunk.api.ChunkDataHandler;
import com.minecolonies.api.colony.IColonyTagCapability;
import com.minecolonies.api.util.Log;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.WeakHashMap;

/**
 * ChunkAPI data handler that attaches {@link IColonyTagCapability} data to each chunk.
 *
 * <p>Register this handler during {@code FMLPreInitializationEvent}:
 * <pre>{@code
 * ChunkDataManager.registerDataHandler(new ColonyChunkDataHandler());
 * }</pre></p>
 *
 * <p><b>1.7.10 Backport:</b> Replaces {@code MinecoloniesChunkCapabilityProvider} and the
 * entire Forge chunk capability system for per-chunk colony data.</p>
 */
public class ColonyChunkDataHandler implements ChunkDataHandler
{
    /** Unique identifier used by ChunkAPI to key the NBT subtag. */
    public static final String HANDLER_ID = "minecolonies_colony_tag";

    /**
     * Per-chunk capability instances.
     *
     * <p>Keyed by {@link Chunk} with weak references so that unloaded chunks are
     * garbage-collected. The ChunkAPI callbacks populate and evict entries.</p>
     */
    private static final WeakHashMap<Chunk, IColonyTagCapability> CHUNK_CAP_MAP = new WeakHashMap<>();

    // -----------------------------------------------------------------------
    // Public accessor (replaces chunk.getCapability(CLOSE_COLONY_CAP, …))
    // -----------------------------------------------------------------------

    /**
     * Returns the {@link IColonyTagCapability} for the given chunk, or {@code null} if the
     * chunk has not been loaded / initialised yet.
     *
     * <p>This is the primary replacement for all former
     * {@code chunk.getCapability(CLOSE_COLONY_CAP, null).resolve().orElse(null)} call-sites.</p>
     */
    @Nullable
    public static IColonyTagCapability getColonyTagCapability(@NotNull final Chunk chunk)
    {
        return CHUNK_CAP_MAP.get(chunk);
    }

    /**
     * Returns the {@link IColonyTagCapability} for the given chunk, creating a new (empty)
     * instance if one does not yet exist.
     */
    @NotNull
    public static IColonyTagCapability getOrCreateColonyTagCapability(@NotNull final Chunk chunk)
    {
        return CHUNK_CAP_MAP.computeIfAbsent(chunk, c -> new IColonyTagCapability.Impl());
    }

    // -----------------------------------------------------------------------
    // ChunkDataHandler implementation
    // -----------------------------------------------------------------------

    @Override
    public String id()
    {
        return HANDLER_ID;
    }

    @Override
    public boolean chunkPrivileged()
    {
        // Not privileged — no unsafe class-patcher operations needed.
        return false;
    }

    /**
     * Called by ChunkAPI when a chunk is loaded from disk.
     * Deserialises the {@link IColonyTagCapability} from the chunk's NBT subtag.
     */
    @Override
    public void readFromNBT(@NotNull final Chunk chunk, @NotNull final NBTTagCompound nbt)
    {
        final IColonyTagCapability cap = new IColonyTagCapability.Impl();
        try
        {
            // [1.7.10 BACKPORT] In 1.21 this was done via
            //   IColonyTagCapability.Storage.readNBT(CLOSE_COLONY_CAP, impl, null, NBTBase)
            // The Capability<> parameter is null here since it doesn't exist in 1.7.10.
            IColonyTagCapability.Storage.readNBT(null, cap, null, nbt);
        }
        catch (final Exception e)
        {
            Log.getLogger().error("ColonyChunkDataHandler: error reading colony NBTBase for chunk "
                + chunk.xPosition + "," + chunk.zPosition, e);
        }
        CHUNK_CAP_MAP.put(chunk, cap);
    }

    /**
     * Called by ChunkAPI when a chunk is saved to disk.
     * Serialises the {@link IColonyTagCapability} into the chunk's NBT subtag.
     */
    @Override
    public void writeToNBT(@NotNull final Chunk chunk, @NotNull final NBTTagCompound nbt)
    {
        final IColonyTagCapability cap = CHUNK_CAP_MAP.get(chunk);
        if (cap == null)
        {
            return; // Nothing to save for this chunk.
        }
        try
        {
            // [1.7.10 BACKPORT] In 1.21 this was done via
            //   IColonyTagCapability.Storage.writeNBT(CLOSE_COLONY_CAP, impl, null)
            final net.minecraft.nbt.NBTBase written = IColonyTagCapability.Storage.writeNBT(null, cap, null);
            if (written instanceof NBTTagCompound)
            {
                final NBTTagCompound src = (NBTTagCompound) written;
                for (final Object key : src.func_150296_c())
                {
                    nbt.setTag((String) key, src.getTag((String) key));
                }
            }
        }
        catch (final Exception e)
        {
            Log.getLogger().error("ColonyChunkDataHandler: error writing colony NBTBase for chunk "
                + chunk.xPosition + "," + chunk.zPosition, e);
        }
    }

    /**
     * Called when the chunk is unloaded / discarded.
     * We remove the entry from the map so the Chunk can be GC'd.
     */
    @Override
    public void onChunkUnloaded(@NotNull final Chunk chunk)
    {
        CHUNK_CAP_MAP.remove(chunk);
    }

    /**
     * Called when a new, empty chunk is generated.
     * Pre-populate with an empty capability so getColonyTagCapability() never returns null
     * for freshly generated chunks.
     */
    @Override
    public void onChunkGenerated(@NotNull final Chunk chunk)
    {
        CHUNK_CAP_MAP.putIfAbsent(chunk, new IColonyTagCapability.Impl());
    }
}


