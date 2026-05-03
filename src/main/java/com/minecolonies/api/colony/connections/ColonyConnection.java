package com.minecolonies.api.colony.connections;

import com.minecolonies.api.util.BlockPosUtil;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

import static com.minecolonies.api.util.constant.NbtTagConstants.*;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_STATUS;

/**
 * Small storage class to hold colony connection data.
 */
public class ColonyConnection
{
    public DiplomacyStatus diplomacyStatus;
    public int                                      id;
    public String                                   name;
    public int[]                                 pos;

    /**
     * Connected Colony Data with:
     *
     * @param id              the colony id.
     * @param name            the colony name (cached).
     * @param pos             the colony gate position (cached).
     * @param diplomacyStatus the diplomacy status of the two colonies.
     */
    public ColonyConnection(
        int id,
        String name,
        int[] pos,
        DiplomacyStatus diplomacyStatus)
    {
        this.id = id;
        this.name = name;
        this.pos = pos;
        this.diplomacyStatus = diplomacyStatus;
    }

    /**
     * Constructor for deserialization/serialization.
     */
    public ColonyConnection()
    {
        // noop
    }

    public NBTTagCompound serializeNBT()
    {
        final NBTTagCompound NBTTagCompound = new NBTTagCompound();
        NBTTagCompound.putInt(TAG_ID, id);
        NBTTagCompound.putString(TAG_NAME, name);
        BlockPosUtil.write(NBTTagCompound, TAG_POS, pos);
        NBTTagCompound.putInt(TAG_STATUS, diplomacyStatus.ordinal());
        return NBTTagCompound;
    }

    public ColonyConnection deserializeNBT(final NBTTagCompound NBTTagCompound)
    {
        this.id = NBTTagCompound.getInt(TAG_ID);
        this.name = NBTTagCompound.getString(TAG_NAME);
        this.pos = BlockPosUtil.read(NBTTagCompound, TAG_POS);
        this.diplomacyStatus = DiplomacyStatus.values()[NBTTagCompound.getInt(TAG_STATUS)];
        return this;
    }

    public void serializeByteBuf(final PacketBuffer buf)
    {
        buf.writeInt(id);
        buf.writeUtf(name);
        buf.writeBlockPos(pos);
        buf.writeInt(diplomacyStatus.ordinal());
    }

    public ColonyConnection deserializeByteBuf(final PacketBuffer buf)
    {
        this.id = buf.readInt();
        this.name = buf.readUtf();
        this.pos = buf.readBlockPos();
        this.diplomacyStatus = DiplomacyStatus.values()[buf.readInt()];
        return this;
    }
}



