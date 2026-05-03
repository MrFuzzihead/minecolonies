package com.minecolonies.core.colony;

// [1.7.10 BACKPORT] Replacement for the IColonyManagerCapability + world capability provider.
//
// In 1.21, the colony manager data was stored as a Forge capability attached to World:
//   world.getCapability(COLONY_MANAGER_CAP, null).resolve().orElse(null)
//
// In 1.7.10, Forge capabilities do not exist. Instead, this WorldSavedData subclass is stored
// in the world's MapStorage (the same mechanism vanilla uses for scoreboard, command block data,
// etc.). It wraps IColonyManagerCapability.Impl exactly so that all the underlying logic is
// unchanged.
//
// Usage (replaces all `world.getCapability(COLONY_MANAGER_CAP, …)` call-sites):
//   IColonyManagerCapability cap = ColonyManagerWorldSavedData.getOrCreate(world).getCapability();
//
// The data is automatically saved by Minecraft whenever markDirty() is called (which the
// IColonyManagerCapability.Storage.writeNBT() path still handles during world saves).

import com.minecolonies.api.util.Log;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * WorldSavedData wrapper around {@link IColonyManagerCapability}.
 *
 * <p>Replaces the 1.21 Forge capability system for world-World colony manager storage.</p>
 */
public class ColonyManagerWorldSavedData extends WorldSavedData
{
    /** The MapStorage key used to identify this data. */
    public static final String SAVE_ID = "minecolonies_colony_manager";

    /**
     * Whether this instance represents the overworld (the dimension that also stores the
     * global IColonyManager state).
     */
    private boolean overworld;

    /** The wrapped capability implementation. */
    @NotNull
    private final IColonyManagerCapability capability = new IColonyManagerCapability.Impl();

    // -----------------------------------------------------------------------
    // Constructors (both required by WorldSavedData)
    // -----------------------------------------------------------------------

    /**
     * No-arg constructor required by {@link WorldSavedData#forClassType(String, Class)}.
     * Overworld flag defaults to false; corrected by the load path.
     */
    public ColonyManagerWorldSavedData()
    {
        super(SAVE_ID);
    }

    public ColonyManagerWorldSavedData(final String id)
    {
        super(id);
    }

    // -----------------------------------------------------------------------
    // Static accessor (replaces world.getCapability(COLONY_MANAGER_CAP, …))
    // -----------------------------------------------------------------------

    /**
     * Returns (or lazily creates) the {@link ColonyManagerWorldSavedData} for the given world.
     *
     * <p>This is the primary replacement for all former
     * {@code world.getCapability(COLONY_MANAGER_CAP, null).resolve().orElse(null)} call-sites.</p>
     *
     * @param world     the world whose colony data to access.
     * @param overworld {@code true} if this is the overworld dimension (dimension 0).
     * @return the data object; never {@code null}.
     */
    @NotNull
    public static ColonyManagerWorldSavedData getOrCreate(@NotNull final World world, final boolean overworld)
    {
        ColonyManagerWorldSavedData data =
            (ColonyManagerWorldSavedData) world.mapStorage.loadData(ColonyManagerWorldSavedData.class, SAVE_ID);
        if (data == null)
        {
            data = new ColonyManagerWorldSavedData();
            data.overworld = overworld;
            world.mapStorage.setData(SAVE_ID, data);
        }
        else
        {
            data.overworld = overworld;
        }
        return data;
    }

    /**
     * Convenience overload that infers the overworld flag from the dimension ID.
     */
    @NotNull
    public static ColonyManagerWorldSavedData getOrCreate(@NotNull final World world)
    {
        return getOrCreate(world, world.provider.dimensionId == 0);
    }

    /**
     * Returns the data if it has already been loaded, or {@code null} if not yet present.
     * Does <em>not</em> create a new instance.
     */
    @Nullable
    public static ColonyManagerWorldSavedData getIfLoaded(@NotNull final World world)
    {
        return (ColonyManagerWorldSavedData) world.mapStorage.loadData(ColonyManagerWorldSavedData.class, SAVE_ID);
    }

    // -----------------------------------------------------------------------
    // Capability access
    // -----------------------------------------------------------------------

    /**
     * Returns the underlying {@link IColonyManagerCapability}.
     *
     * <p>Replaces all former {@code cap = world.getCapability(COLONY_MANAGER_CAP, …)} accesses.</p>
     */
    @NotNull
    public IColonyManagerCapability getCapability()
    {
        return capability;
    }

    /** Convenience: returns {@code true} if this is the overworld dimension instance. */
    public boolean isOverworld()
    {
        return overworld;
    }

    // -----------------------------------------------------------------------
    // WorldSavedData NBT serialization
    // -----------------------------------------------------------------------

    @Override
    public void readFromNBT(@NotNull final NBTTagCompound compound)
    {
        // [1.7.10 BACKPORT] In 1.21 this was done by IColonyManagerCapability.Storage.readNBT()
        // called from MinecoloniesWorldColonyManagerCapabilityProvider.deserializeNBT().
        // We delegate to the same static Storage method (with a null Capability parameter,
        // since the Capability<> type no longer exists in 1.7.10).
        try
        {
            IColonyManagerCapability.Storage.readNBT(null, capability, overworld, compound);
        }
        catch (final Exception e)
        {
            Log.getLogger().error("ColonyManagerWorldSavedData: error reading colony manager NBT", e);
        }
    }

    @Override
    public void writeToNBT(@NotNull final NBTTagCompound compound)
    {
        // [1.7.10 BACKPORT] In 1.21 this was done by IColonyManagerCapability.Storage.writeNBT()
        // called from MinecoloniesWorldColonyManagerCapabilityProvider.serializeNBT().
        try
        {
            final NBTTagCompound written = (NBTTagCompound) IColonyManagerCapability.Storage.writeNBT(null, capability, overworld);
            // Merge all keys into 'compound' (WorldSavedData passes us the compound to fill).
            for (final Object key : written.func_150296_c())
            {
                compound.setTag((String) key, written.getTag((String) key));
            }
        }
        catch (final Exception e)
        {
            Log.getLogger().error("ColonyManagerWorldSavedData: error writing colony manager NBT", e);
        }
    }
}


