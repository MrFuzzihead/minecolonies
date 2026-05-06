package com.minecolonies.core.colony.buildings.modules;

import com.minecolonies.api.colony.buildings.modules.AbstractBuildingModule;
import com.minecolonies.api.colony.buildings.modules.IBuildingEventsModule;
import com.minecolonies.api.colony.buildings.modules.IBuildingModule;
import com.minecolonies.api.colony.buildings.modules.IPersistentModule;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.NBTUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTBase;
import net.minecraft.network.PacketBuffer;
import com.minecolonies.api.util.Tuple;
// [1.7.10] int[] -> int x,y,z

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static com.minecolonies.api.util.constant.NbtTagConstants.*;

/**
 * The enchanters station selection module.
 */
public class EnchanterStationsModule extends AbstractBuildingModule implements IBuildingModule, IPersistentModule, IBuildingEventsModule
{
    /**
     * List of buildings the enchanter gathers experience from.
     */
    private Map<int[], Boolean> buildingToGatherFrom = new HashMap<>();

    /**
     * The random variable.
     */
    private Random random = new Random();

    @Override
    public void deserializeNBT(final NBTTagCompound compound)
    {
        buildingToGatherFrom.clear();
        NBTUtils.streamCompound(compound.getTagList(TAG_GATHER_LIST, NBTBase.TAG_COMPOUND))
          .map(this::deserializeListElement)
          .forEach(t -> buildingToGatherFrom.put(t.getA(), t.getB()));
    }

    @Override
    public void serializeNBT(final NBTTagCompound compound)
    {
        compound.setTag(TAG_GATHER_LIST, buildingToGatherFrom.entrySet().stream().map(this::serializeListElement).collect(NBTUtils.toListNBT()));
    }

    @Override
    public void serializeToView(@NotNull final PacketBuffer buf)
    {
        buf.writeInt(buildingToGatherFrom.size());
        for (final int[] pos : buildingToGatherFrom.keySet())
        {
            buf.writeBlockPos(pos);
        }
    }

    /**
     * Helper to deserialize a list element from nbt.
     *
     * @param nbtTagCompound the compound to deserialize from.
     * @return the resulting int[]/boolean tuple.
     */
    private Tuple<int[], Boolean> deserializeListElement(final NBTTagCompound nbtTagCompound)
    {
        final int[] pos = BlockPosUtil.read(nbtTagCompound, TAG_POS);
        final boolean gatheredAlready = nbtTagCompound.getBoolean(TAG_GATHERED_ALREADY);
        return new Tuple<>(pos, gatheredAlready);
    }

    /**
     * Serialize the element.
     * @param entry the entry to serialize.
     * @return the resulting compound.
     */
    private NBTTagCompound serializeListElement(final Map.Entry<int[], Boolean> entry)
    {
        final NBTTagCompound compound = new NBTTagCompound();
        BlockPosUtil.write(compound, TAG_POS, entry.getKey());
        compound.setBoolean(TAG_GATHERED_ALREADY, entry.getValue());
        return compound;
    }

    /**
     * Return the set of the buildings to gather from.
     *
     * @return a copy of th eset.
     */
    public Set<int[]> getBuildingsToGatherFrom()
    {
        return new HashSet<>(buildingToGatherFrom.keySet());
    }

    /**
     * Get a random worker building id to gather xp from.
     *
     * @return the unique pos id of it.
     */
    @Nullable
    public int[] getRandomBuildingToDrainFrom()
    {
        final List<int[]> buildings = buildingToGatherFrom.entrySet().stream().filter(k -> !k.getValue()).map(Map.Entry::getKey).collect(Collectors.toList());
        if (buildings.isEmpty())
        {
            return null;
        }
        return buildings.get(random.nextInt(buildings.size()));
    }

    /**
     * Set the building as gathered.
     *
     * @param pos the pos of the building.
     */
    public void setAsGathered(final int[] pos)
    {
        buildingToGatherFrom.put(pos, true);
    }

    /**
     * Add a new worker to gather xp from.
     *
     * @param int[] the pos of the building.
     */
    public void addWorker(final int[] blockPos)
    {
        buildingToGatherFrom.put(blockPos, false);
        markDirty();
    }

    /**
     * Remove a worker to stop gathering from.
     *
     * @param int[] the pos of that worker.
     */
    public void removeWorker(final int[] blockPos)
    {
        buildingToGatherFrom.remove(blockPos);
        markDirty();
    }

    @Override
    public void onWakeUp()
    {
        final Set<int[]> keys = new HashSet<>(buildingToGatherFrom.keySet());
        buildingToGatherFrom.clear();
        keys.forEach(k -> buildingToGatherFrom.put(k, false));
    }
}






