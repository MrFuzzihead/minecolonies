package com.minecolonies.core.colony;
import net.minecraft.world.level.chunk.LevelChunk;

// [1.7.10 BACKPORT] Replacement for MinecoloniesChunkCapabilityProvider + IColonyTagCapability
// chunk capability attachment.
//
// In 1.21, per-chunk colony data was stored as a Forge capability attached to LevelChunk.
// In 1.7.10, ChunkAPI (com.falsepattern:chunkapi-mc1.7.10:0.8.2) provides
// DataManager.ChunkDataManager that reads/writes extra NBT data per chunk.
//
// Register with: DataRegistry.registerDataManager(new ColonyChunkDataHandler());

import com.falsepattern.chunk.api.DataManager;
import com.falsepattern.chunk.api.DataRegistry;
import com.minecolonies.api.colony.IColonyTagCapability;
import com.minecolonies.api.util.Log;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.WeakHashMap;

/**
 * ChunkAPI data manager that attaches {@link IColonyTagCapability} data to each chunk.
 *
 * <p>Register this handler during {@code FMLPreInitializationEvent}:
 * <pre>{@code
 * DataRegistry.registerDataManager(new ColonyChunkDataHandler());
 * }</pre></p>
 *
 * <p><b>1.7.10 Backport:</b> Replaces {@code MinecoloniesChunkCapabilityProvider} and the
 * entire Forge chunk capability system for per-chunk colony data.</p>
 */
public class ColonyChunkDataHandler implements DataManager.ChunkDataManager
{
    /** Domain used by ChunkAPI to namespace the NBT subtag. */
    public static final String HANDLER_DOMAIN = "minecolonies";

    /** ID within the domain. */
    public static final String HANDLER_ID = "colony_tag";

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
     * Returns the {@link IColonyTagCapability} for the given chunk, or a new empty instance.
     */
    @NotNull
    public static IColonyTagCapability getColonyTagCapability(@NotNull final Chunk chunk)
    {
        return CHUNK_CAP_MAP.computeIfAbsent(chunk, c -> new IColonyTagCapability.Impl());
    }

    /**
     * Returns the {@link IColonyTagCapability} for the given chunk, or {@code null} if not loaded.
     */
    @Nullable
    public static IColonyTagCapability getColonyTagCapabilityIfPresent(@NotNull final Chunk chunk)
    {
        return CHUNK_CAP_MAP.get(chunk);
    }

    // -----------------------------------------------------------------------
    // DataManager implementation
    // -----------------------------------------------------------------------

    @Override
    public String domain()
    {
        return HANDLER_DOMAIN;
    }

    @Override
    public String id()
    {
        return HANDLER_ID;
    }

    @Override
    public String version()
    {
        return "1";
    }

    @Override
    public String newInstallDescription()
    {
        return "MineColonies colony chunk data initialised.";
    }

    @Override
    public String uninstallMessage()
    {
        return "MineColonies colony chunk data removed.";
    }

    @Override
    public String versionChangeMessage(final String previousVersion)
    {
        return "MineColonies colony chunk data updated from " + previousVersion + " to " + version();
    }

    // -----------------------------------------------------------------------
    // DataManager.ChunkDataManager implementation
    // -----------------------------------------------------------------------

    @Override
    public void readChunkFromNBT(@NotNull final Chunk chunk, @NotNull final NBTTagCompound nbt)
    {
        final IColonyTagCapability cap = new IColonyTagCapability.Impl();
        try
        {
            IColonyTagCapability.Storage.readNBT(null, cap, null, nbt);
        }
        catch (final Exception e)
        {
            Log.getLogger().error("ColonyChunkDataHandler: error reading colony NBT for chunk "
                + chunk.xPosition + "," + chunk.zPosition, e);
        }
        CHUNK_CAP_MAP.put(chunk, cap);
    }

    @Override
    public void writeChunkToNBT(@NotNull final Chunk chunk, @NotNull final NBTTagCompound nbt)
    {
        final IColonyTagCapability cap = CHUNK_CAP_MAP.get(chunk);
        if (cap == null)
        {
            return;
        }
        try
        {
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
            Log.getLogger().error("ColonyChunkDataHandler: error writing colony NBT for chunk "
                + chunk.xPosition + "," + chunk.zPosition, e);
        }
    }

    @Override
    public void cloneChunk(@NotNull final Chunk from, @NotNull final Chunk to)
    {
        final IColonyTagCapability cap = CHUNK_CAP_MAP.get(from);
        if (cap != null)
        {
            // Deep-copy via NBT round-trip
            final NBTTagCompound tmp = new NBTTagCompound();
            writeChunkToNBT(from, tmp);
            readChunkFromNBT(to, tmp);
        }
    }
}

