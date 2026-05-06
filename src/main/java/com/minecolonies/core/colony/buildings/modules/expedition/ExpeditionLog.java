package com.minecolonies.core.colony.buildings.modules.expedition;
import net.minecraft.util.Direction;
// [1.7.10] removed: import net.minecraft.core.Direction; (use net.minecraft.util.Direction)
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;

import com.google.common.base.Enums;
import com.google.common.collect.ImmutableList;
import com.minecolonies.api.colony.requestsystem.StandardFactoryController;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import com.minecolonies.api.util.Tuple;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTBase;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
// [1.7.10] world.entity removed
import net.minecraft.item.ItemStack;
// [1.7.10] registries removed
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * This keeps track of an "expedition", which can be any worker that wanders off somewhere and gathers and fights.
 * It keeps track of the worker's health (and potentially other interesting stats), whatever equipment they're using
 * on the job, what mobs they're interacting with (fighting or otherwise), and any resources they've gathered along
 * the way.  It is intended to be updated "live" while the expedition is in progress and then persist the final state
 * until the next expedition begins.
 */
public class ExpeditionLog
{
    private static final String TAG_STATUS = "status";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_STATS = "stats";
    private static final String TAG_EQUIPMENT = "equip";
    private static final String TAG_MOBS = "mobs";
    private static final String TAG_TYPE = "type";
    private static final String TAG_COUNT = "count";
    private static final String TAG_LOOT = "loot";

    public enum Status
    {
        NONE,
        STARTING,
        IN_PROGRESS,
        RETURNING_HOME,
        COMPLETED,
        KILLED
    }

    // it would be nice to have a more generic way to store these, but they're all split between Data and Attributes
    // and CitizenData and not all of those have names for consistent persistence, or will reliably persist the
    // same values as captured and not more recent values :(
    public enum StatType
    {
        HEALTH,
        SATURATION,
    }

    private Status status = Status.NONE;
    private int id;
    private String name;
    private Map<StatType, Double> stats = new HashMap<>();
    private List<ItemStack> equipment = new ArrayList<>();
    private Map<Class<?>, Integer> mobs = new HashMap<>();
    private Map<ItemStorage, ItemStorage> loot = new HashMap<>();

    /**
     * Resets the expedition log to prepare to start a new expedition.
     * Only call this when the next expedition is actually about to begin, so that the player
     * can see the results of the last expedition for as long as possible.
     */
    public void reset()
    {
        this.status = Status.NONE;
        this.id = 0;
        this.name = null;
        this.stats.clear();
        this.equipment = Collections.emptyList();
        this.mobs.clear();
        this.loot.clear();
    }

    /**
     * Reports the current status of the expedition
     * @return the current expedition status
     */
    public Status getStatus()
    {
        return this.status;
    }

    /**
     * Sets the current status of the expedition
     * @param status the new expedition status
     */
    public void setStatus(final Status status)
    {
        this.status = status;
    }

    /**
     * Reports the id of the citizen who is on this expedition, if any.
     * @return the id, or 0 if there is no citizen or the citizen was killed.
     */
    public int getId()
    {
        return this.id;
    }

    /**
     * Reports the name of the citizen who is (or was) on this expedition, if any.
     * @return the name, or null if there is nobody.  (does not reset when killed)
     */
    @Nullable
    public String getName()
    {
        return this.name;
    }

    /**
     * Reports the stats of the citizen.
     * @param stat the stat to retrieve
     * @return the value of that stat (as of the latest update, not necessarily "live")
     */
    public double getStat(final StatType stat)
    {
        return this.stats.getOrDefault(stat, 0.0);
    }

    /**
     * Captures and updates the stats and other info for the citizen currently on the expedition.
     * @param citizen the citizen who is currently on the expedition
     */
    public void setCitizen(@Nullable final AbstractEntityCitizen citizen)
    {
        if (citizen == null)
        {
            this.id = 0;
            this.name = null;
            this.stats.clear();
        }
        else
        {
            this.id = citizen.getId();
            this.name = citizen.getCitizenData().getName();
            this.stats.put(StatType.HEALTH, (double) citizen.getHealth());
            this.stats.put(StatType.SATURATION, citizen.getCitizenData().getSaturation());
        }
    }

    /**
     * Indicates that the citizen was killed while on the expedition (which ends it).
     */
    public void setKilled()
    {
        this.id = 0;
        this.status = Status.KILLED;
    }

    /**
     * Reports the list of equipment currently in use on this expedition.
     * @return the equipment list.  Some stacks might be empty.
     */
    public List<ItemStack> getEquipment()
    {
        return this.equipment;
    }

    /**
     * Captures and sets the list of equipment currently in use on this expedition.
     * Some stacks may be empty to indicate that there is no equipment in that "slot".
     * It's up to the actual expedition to decide what equipment is interesting to show.
     * @param equipment the list of equipment
     */
    public void setEquipment(@NotNull final List<ItemStack> equipment)
    {
        this.equipment = equipment.stream()
                .map(ItemStack::copy)
                .collect(ImmutableList.toImmutableList());
    }

    /**
     * Reports the list and count of mobs interacted with (usually fought, but doesn't have to be) during
     * this expedition.
     * @return the list of mobs and counts, sorted highest-count-first
     */
    public List<Tuple<Class<?>, Integer>> getMobs()
    {
        return this.mobs.entrySet().stream()
                .map(entry -> new Tuple<Class<?>, Integer>(entry.getKey(), entry.getValue()))
                .sorted(Comparator.<Tuple<Class<?>, Integer>>comparingInt(Tuple::getB).reversed())
                .collect(ImmutableList.toImmutableList());
    }

    /**
     * Adds a EntityCreature to the interaction list of this expedition.
     * @param mobType the type of EntityCreature
     */
    public void addMob(@NotNull final Class<?> mobType)
    {
        this.mobs.merge(mobType, 1, Integer::sum);
    }

    /**
     * Reports the resources gathered so far during this expedition.
     * @return the list of resources, sorted highest-amount-first
     */
    public List<ItemStorage> getLoot()
    {
        return this.loot.keySet().stream()
                .sorted(Comparator.comparing(ItemStorage::getAmount).reversed())
                .collect(ImmutableList.toImmutableList());
    }

    /**
     * Adds a list of resources to this expedition.
     * @param loot the additional resources gathered this round
     */
    public void addLoot(@NotNull final List<ItemStack> loot)
    {
        for (final ItemStack stack : loot)
        {
            ItemStorage storage = new ItemStorage(stack);
            this.loot.merge(storage, storage, (o, n) ->
            {
                o.setAmount(o.getAmount() + n.getAmount());
                return o;
            });
        }
    }

    /**
     * Save to NBT
     * @param compound target
     */
    public void serializeNBT(@NotNull final NBTTagCompound compound)
    {
        compound.putString(TAG_STATUS, this.status.name());
        compound.putInt(TAG_ID, this.id);
        compound.putString(TAG_NAME, this.name == null ? "" : this.name);

        final NBTTagCompound stats = new NBTTagCompound();
        for (final Map.Entry<StatType, Double> entry : this.stats.entrySet())
        {
            stats.putDouble(entry.getKey().name().toLowerCase(Locale.US), entry.getValue());
        }
        compound.setTag(TAG_STATS, stats);

        final NBTTagList equipment = new NBTTagList();
        for (final ItemStack stack : this.equipment)
        {
            equipment.add(stack.serializeNBT());
        }
        compound.setTag(TAG_EQUIPMENT, equipment);

        final NBTTagList mobs = new NBTTagList();
        for (final Map.Entry<Class<?>, Integer> entry : this.mobs.entrySet())
        {
            final NBTTagCompound EntityCreature = new NBTTagCompound();
            EntityCreature.putString(TAG_TYPE, ForgeRegistries.ENTITY_TYPES.getKey(entry.getKey()).toString());
            EntityCreature.putInt(TAG_COUNT, entry.getValue());
            mobs.add(EntityCreature);
        }
        compound.setTag(TAG_MOBS, mobs);

        final NBTTagList loot = new NBTTagList();
        for (final ItemStorage storage : this.loot.values())
        {
            loot.add(StandardFactoryController.getInstance().serialize(storage));
        }
        compound.setTag(TAG_LOOT, loot);
    }

    /**
     * Reload from NBT
     * @param compound source
     */
    public void deserializeNBT(@NotNull final NBTTagCompound compound)
    {
        this.status = Enums.getIfPresent(Status.class, compound.getString(TAG_STATUS)).or(Status.NONE);
        this.id = compound.getInt(TAG_ID);
        this.name = compound.getString(TAG_NAME);
        if (this.name.isEmpty()) this.name = null;

        this.stats.clear();
        final NBTTagCompound stats = compound.getCompoundTag(TAG_STATS);
        for (final StatType stat : StatType.values())
        {
            final String key = stat.name().toLowerCase(Locale.US);
            if (stats.contains(key))
            {
                this.stats.put(stat, stats.getDouble(key));
            }
        }

        this.equipment.clear();
        final NBTTagList equipment = compound.getTagList(TAG_EQUIPMENT, NBTBase.TAG_COMPOUND);
        for (int i = 0; i < equipment.size(); i++)
        {
            this.equipment.add(ItemStack.of(equipment.getCompoundTagAt(i)));
        }

        this.mobs.clear();
        final NBTTagList mobs = compound.getTagList(TAG_MOBS, NBTBase.TAG_COMPOUND);
        for (int i = 0; i < mobs.size(); ++i)
        {
            final NBTTagCompound EntityCreature = mobs.getCompoundTagAt(i);
            final ResourceLocation type = new ResourceLocation(EntityCreature.getString(TAG_TYPE));
            final Class<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(type);
            if (entityType != null)
            {
                this.mobs.put(entityType, EntityCreature.getInt(TAG_COUNT));
            }
        }

        this.loot.clear();
        final NBTTagList loot = compound.getTagList(TAG_LOOT, NBTBase.TAG_COMPOUND);
        for (int i = 0; i < loot.size(); i++)
        {
            final ItemStorage storage = StandardFactoryController.getInstance().deserialize(loot.getCompoundTagAt(i));
            this.loot.put(storage, storage);
        }
    }

    /**
     * Save to network
     * @param buf target
     */
    public void serialize(@NotNull final PacketBuffer buf)
    {
        buf.writeVarInt(this.status.ordinal());
        buf.writeVarInt(this.id);
        buf.writeUtf(this.name == null ? "" : this.name);

        for (final StatType stat : StatType.values())
        {
            buf.writeDouble(this.stats.getOrDefault(stat, 0.0));
        }

        buf.writeVarInt(this.equipment.size());
        for (final ItemStack stack : this.equipment)
        {
            buf.writeItem(stack);
        }

        buf.writeVarInt(this.mobs.size());
        for (final Map.Entry<Class<?>, Integer> entry : this.mobs.entrySet())
        {
            buf.writeRegistryIdUnsafe(ForgeRegistries.ENTITY_TYPES, entry.getKey());
            buf.writeVarInt(entry.getValue());
        }

        buf.writeVarInt(this.loot.size());
        for (final ItemStorage storage : this.loot.values())
        {
            StandardFactoryController.getInstance().serialize(buf, storage);
        }
    }

    /**
     * Reload from network
     * @param buf source
     */
    public void deserialize(@NotNull final PacketBuffer buf)
    {
        this.status = Status.values()[buf.readVarInt()];
        this.id = buf.readVarInt();
        this.name = buf.readUtf();
        if (this.name.isEmpty()) this.name = null;

        this.stats.clear();
        for (final StatType stat : StatType.values())
        {
            this.stats.put(stat, buf.readDouble());
        }

        this.equipment.clear();
        for (int size = buf.readVarInt(); size > 0; --size)
        {
            this.equipment.add(buf.readItem());
        }

        this.mobs.clear();
        for (int size = buf.readVarInt(); size > 0; --size)
        {
            final Class<?> entityType = buf.readRegistryIdUnsafe(ForgeRegistries.ENTITY_TYPES);
            final int count = buf.readVarInt();
            if (entityType != null)
            {
                this.mobs.put(entityType, count);
            }
        }

        this.loot.clear();
        for (int size = buf.readVarInt(); size > 0; --size)
        {
            final ItemStorage storage = StandardFactoryController.getInstance().deserialize(buf);
            this.loot.put(storage, storage);
        }
    }
}





