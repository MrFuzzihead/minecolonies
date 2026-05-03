package com.minecolonies.api.colony.colonyEvents.registry;

import com.minecolonies.api.colony.colonyEvents.descriptions.IColonyEventDescription;
import com.minecolonies.api.util.Log;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.function.Function;

/**
 * This is the colonies event registry entry class, used for registering any colony related events. Takes a function of colony, nbt to create the right event object.
 */
public class ColonyEventDescriptionTypeRegistryEntry
{
    /**
     * Function for creating the event description from nbt.
     */
    private final Function<NBTTagCompound, IColonyEventDescription> nbtEventDescriptionCreator;

    /**
     * Function for creating the event description from a {@link PacketBuffer}.
     */
    private final Function<PacketBuffer, IColonyEventDescription> packetBufferEventDescriptionCreator;

    /**
     * The name of this registry.
     */
    private final ResourceLocation registryName;

    /**
     * Creates a new registry entry for the given function and registry name.
     *
     * @param nbtEventCreator          the event creator using nbt.
     * @param packetBufferEventCreator the event creator using a {@link PacketBuffer}.
     * @param registryID               the registry id.
     */
    public ColonyEventDescriptionTypeRegistryEntry(@NotNull final Function<NBTTagCompound, IColonyEventDescription> nbtEventCreator, @NotNull final Function<PacketBuffer, IColonyEventDescription> packetBufferEventCreator, @NotNull final ResourceLocation registryID)
    {
        if (registryID.getPath().isEmpty())
        {
            Log.getLogger().warn("Created empty registry empty for event, supply a name for it!");
        }

        nbtEventDescriptionCreator = nbtEventCreator;
        packetBufferEventDescriptionCreator = packetBufferEventCreator;
        registryName = registryID;
    }

    /**
     * Deserializes the event description from nbt.
     * 
     * @param compound the nbt to deserialize the event description from.
     * @return the deserialized event description.
     */
    public IColonyEventDescription deserializeEventDescriptionFromNBT(@Nonnull final NBTTagCompound compound)
    {
        return nbtEventDescriptionCreator.apply(compound);
    }

    /**
     * Deserializes the event description from the given {@link PacketBuffer}.
     * 
     * @param buffer the {@link PacketBuffer} to deserialize the event description from.
     * @return the deserialized event description.
     */
    public IColonyEventDescription deserializeEventDescriptionFromFriendlyByteBuf(@Nonnull final PacketBuffer buffer)
    {
        return packetBufferEventDescriptionCreator.apply(buffer);
    }

    /**
     * Get the set registry name.
     * @return the name.
     */
    public ResourceLocation getRegistryName()
    {
        return registryName;
    }
}



