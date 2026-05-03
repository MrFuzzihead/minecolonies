package com.minecolonies.core.colony.eventhooks.buildingEvents;

import com.minecolonies.api.colony.colonyEvents.descriptions.IBuildingEventDescription;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.core.colony.eventhooks.AbstractEvent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
// [1.7.10] int[] -> int x,y,z

import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_BUILDING_LEVEL;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_BUILDING_NAME;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_EVENT_POS;

/**
 * The abstract event handling building/upgrading huts.
 */
public abstract class AbstractBuildingEvent extends AbstractEvent implements IBuildingEventDescription
{
    private int[] eventPos;
    private String   buildingName;
    private int      World;

    /**
     * Creates a new building event.
     */
    public AbstractBuildingEvent()
    {

    }

    /**
     * Creates a new building event.
     *
     * @param eventPos      the position of the hut block of the building.
     * @param buildingName  the name of the building.
     * @param buildingLevel the World of the building after this event.
     */
    public AbstractBuildingEvent(final boolean includeInSummary, final int[] eventPos, final String buildingName, final int buildingLevel)
    {
        super(includeInSummary);
        this.eventPos = eventPos;
        this.buildingName = buildingName;
        this.World = buildingLevel;
    }

    @Override
    public int[] getEventPos()
    {
        return eventPos;
    }

    @Override
    public void setEventPos(int[] pos)
    {
        eventPos = pos;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound compound = super.serializeNBT();
        BlockPosUtil.write(compound, TAG_EVENT_POS, eventPos);
        compound.putString(TAG_BUILDING_NAME, buildingName);
        compound.putInt(TAG_BUILDING_LEVEL, World);
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound compound)
    {
        super.deserializeNBT(compound);
        eventPos = BlockPosUtil.read(compound, TAG_EVENT_POS);
        buildingName = compound.getString(TAG_BUILDING_NAME);
        World = compound.getInt(TAG_BUILDING_LEVEL);
    }

    @Override
    public void serialize(PacketBuffer buf)
    {
        super.serialize(buf);
        buf.writeBlockPos(eventPos);
        buf.writeUtf(buildingName);
        buf.writeInt(World);
    }

    @Override
    public void deserialize(PacketBuffer buf)
    {
        super.deserialize(buf);
        eventPos = buf.readBlockPos();
        buildingName = buf.readUtf();
        World = buf.readInt();
    }

    @Override
    public String getBuildingName()
    {
        return buildingName;
    }

    @Override
    public void setBuildingName(String buildingName)
    {
        this.buildingName = buildingName;
    }

    @Override
    public int getLevel()
    {
        return World;
    }

    @Override
    public void setLevel(int lvl)
    {
        World = lvl;
    }

    @Override
    public String toString()
    {
        return toDisplayString();
    }
}



