package com.minecolonies.core.tileentities;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.tileentities.ITickable;
import com.minecolonies.api.util.WorldUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import org.jetbrains.annotations.Nullable;

import static com.minecolonies.api.util.constant.NbtTagConstants.*;

/**
 * TileEntity for the colony sign block.
 */
public class TileEntityColonySign extends TileEntity implements ITickable
{
    /**
     * Connected colony id.
     */
    private int colonyId = -1;

    /**
     * Target colony id we're trying to connect to.
     */
    private int targetColonyId = -1;

    /**
     * Anchor position to which it's supposed to point (as [x, y, z]).
     */
    @Nullable
    private int[] anchor = null;

    /**
     * Colony name cache.
     */
    private String colonyNameCache = "";

    /**
     * Target colony name cache.
     */
    private String targetColonyNameCache = "";

    public TileEntityColonySign()
    {
        super();
    }

    @Override
    public void tick()
    {
        if (worldObj == null || worldObj.isRemote)
        {
            return;
        }

        // TODO: Port full colony sign tick logic (colony connection search)
    }

    @Override
    public void updateEntity()
    {
        tick();
    }

    @Override
    public void writeToNBT(final NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setInteger(TAG_COLONY_ID, colonyId);
        compound.setInteger("targetColonyId", targetColonyId);
        if (anchor != null)
        {
            compound.setIntArray("anchor", anchor);
        }
        compound.setString("colonyName", colonyNameCache);
        compound.setString("targetColonyName", targetColonyNameCache);
    }

    @Override
    public void readFromNBT(final NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        colonyId = compound.getInteger(TAG_COLONY_ID);
        targetColonyId = compound.getInteger("targetColonyId");
        if (compound.hasKey("anchor"))
        {
            anchor = compound.getIntArray("anchor");
        }
        colonyNameCache = compound.getString("colonyName");
        targetColonyNameCache = compound.getString("targetColonyName");
    }

    @Override
    public void markDirty()
    {
        if (worldObj != null)
        {
            WorldUtil.markChunkDirty(worldObj, xCoord, yCoord, zCoord);
        }
    }

    public int getColonyId() { return colonyId; }
    public int getTargetColonyId() { return targetColonyId; }
    public String getColonyNameCache() { return colonyNameCache; }
    public String getTargetColonyNameCache() { return targetColonyNameCache; }
    @Nullable public int[] getAnchor() { return anchor; }
}
