package com.minecolonies.api.colony.connections;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

import static com.minecolonies.api.util.constant.NbtTagConstants.*;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_STATUS;

/**
 * Connected Event Data with:
 *
 * @param id              the colony id.
 * @param connectionEventType the event type enum.
 */
public record ConnectionEvent(int id, String name, ConnectionEventType connectionEventType)
{
    public NBTTagCompound serializeNBT()
    {
        final NBTTagCompound NBTTagCompound = new NBTTagCompound();
        NBTTagCompound.putInt(TAG_ID, id);
        NBTTagCompound.putString(TAG_NAME, name);
        NBTTagCompound.putInt(TAG_STATUS, connectionEventType.ordinal());
        return NBTTagCompound;
    }

    public void serializeByteBuf(final PacketBuffer buf)
    {
        buf.writeInt(id);
        buf.writeUtf(name);
        buf.writeInt(connectionEventType.ordinal());
    }

    public static ConnectionEvent deserializeNBT(final NBTTagCompound NBTTagCompound)
    {
        return new ConnectionEvent(NBTTagCompound.getInt(TAG_ID),
            NBTTagCompound.getString(TAG_NAME),
            ConnectionEventType.values()[NBTTagCompound.getInt(TAG_STATUS)]);
    }

    public static ConnectionEvent deserializeByteBuf(final PacketBuffer buf)
    {
        return new ConnectionEvent(buf.readInt(), buf.readUtf(32767), ConnectionEventType.values()[buf.readInt()]);
    }
}



