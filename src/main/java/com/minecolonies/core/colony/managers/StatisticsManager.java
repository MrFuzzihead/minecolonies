package com.minecolonies.core.colony.managers;

import com.minecolonies.api.colony.managers.interfaces.IStatisticsManager;
import it.unimi.dsi.fastutil.ints.Int2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTBase;
import net.minecraft.network.PacketBuffer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.minecolonies.api.util.constant.NbtTagConstants.*;

/**
 * Manager for colony related statistics.
 */
public class StatisticsManager implements IStatisticsManager
{
    /**
     * NBT tags.
     */
    private static final String TAG_STAT_MANAGER = "stat_manager";
    private static final String TAG_STAT         = "stat";

    /**
     * The current stats of the colony.
     */
    private final Map<String, Int2IntLinkedOpenHashMap> stats = new HashMap<>();

    /**
     * The modified and not yet sent stats
     */
    private Set<String> dirtyStats = new HashSet<>();

    @Override
    public void increment(final @NotNull String id, final int day)
    {
        incrementBy(id, 1, day);
    }

    @Override
    public void incrementBy(final @NotNull String id, int qty, final int day)
    {
        final Int2IntLinkedOpenHashMap innerMap = stats.computeIfAbsent(id, k -> new Int2IntLinkedOpenHashMap());
        innerMap.addTo(day, qty);
        dirtyStats.add(id);
    }

    @Override
    public int getStatTotal(final @NotNull String id)
    {
        final Int2IntLinkedOpenHashMap stats = this.stats.getOrDefault(id, new Int2IntLinkedOpenHashMap());
        int totalCount = 0;
        for (final int count : stats.values())
        {
            totalCount += count;
        }
        return totalCount;
    }

    @Override
    public int getStatsInPeriod(final @NotNull String id, final int startDay, final int endDay)
    {
        final Int2IntLinkedOpenHashMap stats = this.stats.getOrDefault(id, new Int2IntLinkedOpenHashMap());
        int count = 0;
        for (int day = startDay; day <= endDay; day++)
        {
            count += stats.get(day);
        }
        return count;
    }

    @Override
    public @NotNull Set<String> getStatTypes()
    {
        return stats.keySet();
    }

    /**
     * Gets all the current stat entries in this manager.
     * @return a set of entries with the id and the stats map.
     */
    @Override
    public @NotNull Set<Map.Entry<String, Int2IntLinkedOpenHashMap>>  getStatEntries()
    {
        return stats.entrySet();
    }

    /**
     * Clear all the statistics, this will remove all the entries from the map
     */
    @Override
    public void clear()
    {
        stats.clear();
        dirtyStats = new HashSet<>();
    }

    @Override
    public void serialize(@NotNull final PacketBuffer buf, final boolean fullSync)
    {
        buf.writeBoolean(fullSync);
        buf.writeVarInt(fullSync ? stats.size() : dirtyStats.size());

        if (fullSync)
        {
            for (final Map.Entry<String, Int2IntLinkedOpenHashMap> dataEntry : stats.entrySet())
            {
                buf.writeUtf(dataEntry.getKey());
                buf.writeVarInt(dataEntry.getValue().size());

                for (final Int2IntMap.Entry valueEntry : dataEntry.getValue().int2IntEntrySet())
                {
                    buf.writeVarInt(valueEntry.getIntKey());
                    buf.writeVarInt(valueEntry.getIntValue());
                }
            }
        }
        else
        {
            for (final String id : dirtyStats)
            {
                var dataEntry = stats.get(id);

                buf.writeUtf(id);
                buf.writeVarInt(1);
                buf.writeVarInt(dataEntry.lastIntKey());
                buf.writeVarInt(dataEntry.get(dataEntry.lastIntKey()));
            }
        }

        if (!dirtyStats.isEmpty())
        {
            dirtyStats = new HashSet<>();
        }
    }

    @Override
    public void deserialize(@NotNull final PacketBuffer buf)
    {
        final boolean fullSync = buf.readBoolean();
        if (fullSync)
        {
            stats.clear();
        }

        final int statSize = buf.readVarInt();
        for (int i = 0; i < statSize; i++)
        {
            final String id = buf.readUtf();
            final int statEntrySize = buf.readVarInt();

            final Int2IntLinkedOpenHashMap statValues = (fullSync || !stats.containsKey(id)) ? new Int2IntLinkedOpenHashMap(statEntrySize) : stats.get(id);
            for (int j = 0; j < statEntrySize; j++)
            {
                statValues.put(buf.readVarInt(), buf.readVarInt());
            }

            stats.put(id, statValues);
        }
    }

    @Override
    public void writeToNBT(@NotNull final NBTTagCompound compound)
    {
        final NBTTagList statManagerNBT = new NBTTagList();
        for (final Map.Entry<String, Int2IntLinkedOpenHashMap> stat : stats.entrySet())
        {
            final NBTTagCompound statCompound = new NBTTagCompound();
            statCompound.putString(TAG_ID, stat.getKey());

            final NBTTagList statNBT = new NBTTagList();
            for (final Map.Entry<Integer, Integer> dailyStats : stat.getValue().entrySet())
            {
                final NBTTagCompound timeStampTag = new NBTTagCompound();

                timeStampTag.putInt(TAG_TIME, dailyStats.getKey());
                timeStampTag.putInt(TAG_QUANTITY, dailyStats.getValue());

                statNBT.add(timeStampTag);
            }

            statcompound.setTag(TAG_STAT, statNBT);
            statManagerNBT.add(statCompound);
        }

        compound.setTag(TAG_STAT_MANAGER, statManagerNBT);
    }

    @Override
    public void readFromNBT(@NotNull final NBTTagCompound compound)
    {
        stats.clear();
        if (compound.hasKey(TAG_STAT_MANAGER))
        {
            final NBTTagList statsNbts = compound.getTagList(TAG_STAT_MANAGER, NBTBase.TAG_COMPOUND);
            for (int i = 0; i < statsNbts.size(); i++)
            {
                final NBTTagCompound statCompound = statsNbts.getCompoundTagAt(i);
                final String id = statCompound.getString(TAG_ID);
                final NBTTagList timeStampNbts = statCompound.getTagList(TAG_STAT, NBTBase.TAG_COMPOUND);
                final Int2IntLinkedOpenHashMap timeStamps = new Int2IntLinkedOpenHashMap();
                for (int j = 0; j < timeStampNbts.size(); j++)
                {
                    final NBTTagCompound NBTTagCompound = timeStampNbts.getCompoundTagAt(j);
                    final int day = NBTTagCompound.getInt(TAG_TIME);
                    final int qty = NBTTagCompound.getInt(TAG_QUANTITY);

                    timeStamps.put(day, qty);
                }

                stats.put(id, timeStamps);
            }
        }
    }
}




