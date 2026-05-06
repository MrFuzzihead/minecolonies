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
public class ConnectionEvent // [1.7.10] record -> class
{
    public final int id;
    public final String name;
    public final ConnectionEventType connectionEventType;

    public ConnectionEvent(int id, String name, ConnectionEventType connectionEventType)
    {
        this.id = id;
        this.name = name;
        this.connectionEventType = connectionEventType;
    }

    public NBTTagCompound serializeNBT()
    {
        final NBTTagCompound compound = new NBTTagCompound(); // [1.7.10] renamed to avoid shadowing class
        compound.setInteger(TAG_ID, id);
        compound.setString(TAG_NAME, name);
        compound.setInteger(TAG_STATUS, connectionEventType.ordinal());
        return compound;
    }

    public void serializeByteBuf(final PacketBuffer buf)
    {
        buf.writeInt(id);
        try { buf.writeStringToBuffer(name); } catch (java.io.IOException e) { throw new RuntimeException(e); }
        buf.writeInt(connectionEventType.ordinal());
    }

    public static ConnectionEvent deserializeNBT(final NBTTagCompound compound) // [1.7.10] renamed
    {
        return new ConnectionEvent(compound.getInteger(TAG_ID),
            compound.getString(TAG_NAME),
            ConnectionEventType.values()[compound.getInteger(TAG_STATUS)]);
    }

    public static ConnectionEvent deserializeByteBuf(final PacketBuffer buf)
    {
        try
        {
            return new ConnectionEvent(buf.readInt(), buf.readStringFromBuffer(32767), ConnectionEventType.values()[buf.readInt()]); // [1.7.10]
        }
        catch (java.io.IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}


