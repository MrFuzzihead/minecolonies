package com.minecolonies.core.colony.managers;

import com.minecolonies.api.MinecoloniesAPIProxy;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.colonyEvents.descriptions.IColonyEventDescription;
import com.minecolonies.api.colony.colonyEvents.registry.ColonyEventDescriptionTypeRegistryEntry;
import com.minecolonies.api.colony.managers.interfaces.IEventDescriptionManager;
import com.minecolonies.api.util.Log;
import com.minecolonies.api.util.MessageUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;

import static com.minecolonies.api.util.constant.ColonyConstants.MAX_COLONY_EVENTS;
import static com.minecolonies.api.util.constant.Constants.MOD_ID;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_NAME;

/**
 * Manager for all colony related events.
 */
public class EventDescriptionManager implements IEventDescriptionManager
{
    /**
     * NBT tags
     */
    private static final String TAG_EVENT_DESC_LIST    = "event_descs_list";

    /**
     * Colony reference
     */
    private final IColony colony;

    /**
     * The event descriptions of this colony.
     */
    private final ArrayDeque<IColonyEventDescription> eventDescs = new ArrayDeque<>();

    public EventDescriptionManager(final IColony colony)
    {
        this.colony = colony;
    }

    @Override
    public void addEventDescription(@NotNull final IColonyEventDescription colonyEventDescription)
    {
        if (eventDescs.size() >= MAX_COLONY_EVENTS)
        {
            eventDescs.poll();
        }
        colonyEventDescription.setDay(colony.getDay());
        eventDescs.add(colonyEventDescription);
        if (colony.getServerBuildingManager().getTownHall() != null)
        {
            colony.getServerBuildingManager().getTownHall().markDirty();
        }
        else
        {
            colony.markDirty();
        }
    }

    @Override
    public void serialize(@NotNull final PacketBuffer buf)
    {
        buf.writeInt(eventDescs.size());
        for (final IColonyEventDescription event : eventDescs)
        {
            buf.writeUtf(event.getEventTypeId().getPath());
            event.serialize(buf);
        }
    }

    @Override
    public void deserializeNBT(@NotNull final NBTTagCompound eventManagerNBT)
    {
        final NBTTagList eventDescListNBT = eventManagerNBT.getList(TAG_EVENT_DESC_LIST, NBTBase.TAG_COMPOUND);
        for (final NBTBase event : eventDescListNBT)
        {
            final NBTTagCompound eventCompound = (NBTTagCompound) event;
            final ResourceLocation eventTypeID = new ResourceLocation(MOD_ID, eventCompound.getString(TAG_NAME));

            final ColonyEventDescriptionTypeRegistryEntry registryEntry = MinecoloniesAPIProxy.getInstance().getColonyEventDescriptionRegistry().getValue(eventTypeID);
            if (registryEntry == null)
            {
                Log.getLogger().warn("Event is missing registryEntry!:" + eventTypeID.getPath());
                continue;
            }

            final IColonyEventDescription eventDescription = registryEntry.deserializeEventDescriptionFromNBT(eventCompound);
            eventDescs.add(eventDescription);
        }
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        final NBTTagCompound eventManagerNBT = new NBTTagCompound();
        final NBTTagList eventDescsListNBT = new NBTTagList();
        for (final IColonyEventDescription event : eventDescs)
        {
            final NBTTagCompound eventNBT = event.serializeNBT();
            eventNBT.putString(TAG_NAME, event.getEventTypeId().getPath());
            eventDescsListNBT.add(eventNBT);
        }

        eventManagerNBT.put(TAG_EVENT_DESC_LIST, eventDescsListNBT);
        return eventManagerNBT;
    }

    @Override
    public void computeNews()
    {
        final Object2IntMap<String> summaries = new Object2IntOpenHashMap<>();
        for (final IColonyEventDescription event : eventDescs)
        {
            if (event.includeInSummary() && event.getDay() == colony.getDay())
            {
                summaries.compute(event.getSummaryTranslationKey(), (key, value) -> value == null ? 1 :  value + 1);
            }
        }

        MessageUtils.MessageBuilder builder = null;
        for (final Object2IntMap.Entry<String> entry : summaries.object2IntEntrySet())
        {
            if (builder == null)
            {
                builder = MessageUtils.format(String.translatable("com.minecolonies.core.event.summary.prefix")).append(String.translatable(entry.getKey(), entry.getIntValue()));
            }
            else
            {
                builder = builder.append(String.literal(", ")).append(String.translatable(entry.getKey(), entry.getIntValue()));
            }
        }

        if (builder != null)
        {
            builder.append(String.literal("!"));
            builder.sendTo(colony.getImportantMessageEntityPlayers());
        }
    }
}




