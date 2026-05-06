package com.minecolonies.api.colony.colonyEvents.descriptions;

// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
// [1.7.10] INBTSerializable -> manual read/write

/**
 * Description for an event that happened in the colony.
 */
public interface IColonyEventDescription {
    /**
     * Gets this event types registry id.
     *
     * @return this event types registry id.
     */
    ResourceLocation getEventTypeId();

    /**
     * Gets the name of this event type.
     *
     * @return the name of this event type.
     */
    String getName();

    /**
     * Builds the string to show in the colony events list.
     *
     * @return the string to show in the colony events list.
     */
    default String toDisplayString()
    {
        return String.format("%s at %d %d %d.%n", getName(), getEventPos()[0], getEventPos()[1], getEventPos()[2]);
    }

    /**
     * Returns the position at which this event occurred.
     *
     * @return the position at which this event occurred.
     */
    int[] getEventPos();

    /**
     * Sets the position this event happened at.
     *
     * @param pos the position this event happened at.
     */
    void setEventPos(int[] pos);

    /**
     * Serializes this event to the given {@link PacketBuffer}.
     *
     * @param buf the {@link PacketBuffer} to serialize to.
     */
    void serialize(final PacketBuffer buf);

    /**
     * Deserializes this event from the given {@link PacketBuffer}.
     *
     * @param buf the {@link PacketBuffer} to deserialize from.
     */
    void deserialize(final PacketBuffer buf);

    /**
     * Get the day the event occured.
     * @return true.
     */
    int getDay();

    /**
     * Set the day it happened at.
     * @param day the day.
     */
    void setDay(int day);

    /**
     * If the event should be included at the end of day summary.
     * @return true if so.
     */
    boolean includeInSummary();

    /**
     * Get the summary translation key.
     * @return the translation key.
     */
    default String getSummaryTranslationKey()
    {
        // Not all need it.
        return  "";
    }
}






