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
        final NBTTagCompound compound = new NBTTagCompound(); // [1.7.10] renamed to avoid shadowing class
        compound.setInteger(TAG_ID, id);
        compound.setString(TAG_NAME, name);
        BlockPosUtil.write(compound, TAG_POS, pos);
        compound.setInteger(TAG_STATUS, diplomacyStatus.ordinal());
        return compound;
    }

    public ColonyConnection deserializeNBT(final NBTTagCompound compound) // [1.7.10] renamed parameter
    {
        this.id = compound.getInteger(TAG_ID);
        this.name = compound.getString(TAG_NAME);
        this.pos = BlockPosUtil.read(compound, TAG_POS);
        this.diplomacyStatus = DiplomacyStatus.values()[compound.getInteger(TAG_STATUS)];
        return this;
    }

    public void serializeByteBuf(final PacketBuffer buf)
    {
        buf.writeInt(id);
        try { buf.writeStringToBuffer(name); } catch (java.io.IOException e) { throw new RuntimeException(e); }
        // [1.7.10] writeBlockPos -> write x,y,z separately
        buf.writeInt(pos != null ? pos[0] : 0);
        buf.writeInt(pos != null ? pos[1] : 0);
        buf.writeInt(pos != null ? pos[2] : 0);
        buf.writeInt(diplomacyStatus.ordinal());
    }

    public ColonyConnection deserializeByteBuf(final PacketBuffer buf)
    {
        this.id = buf.readInt();
        try { this.name = buf.readStringFromBuffer(32767); } catch (java.io.IOException e) { throw new RuntimeException(e); }
        // [1.7.10] readBlockPos -> read x,y,z separately
        this.pos = new int[]{buf.readInt(), buf.readInt(), buf.readInt()};
        this.diplomacyStatus = DiplomacyStatus.values()[buf.readInt()];
        return this;
    }
}



