package com.minecolonies.core.tileentities;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

import static com.minecolonies.api.util.constant.NbtTagConstants.*;

/**
 * TileEntity for the colony banner/flag block.
 * Note: BannerPattern registry does not exist in 1.7.10 — banner rendering is stubbed.
 */
public class TileEntityColonyFlag extends TileEntity
{
    /**
     * A list of the banner patterns (NBT list format compatible with 1.7.10 banner TE).
     */
    // TODO: no BannerPattern registry in 1.7.10 — patterns stored as raw NBT
    private NBTTagList patterns = new NBTTagList();

    /**
     * The colony of the player that placed this banner.
     */
    public int colonyId = -1;

    public TileEntityColonyFlag()
    {
        super();
    }

    @Override
    public void writeToNBT(final NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setTag(TAG_BANNER_PATTERNS, this.patterns);
        compound.setInteger(TAG_COLONY_ID, colonyId);
    }

    @Override
    public void readFromNBT(final NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.patterns = compound.getTagList(TAG_BANNER_PATTERNS, 10);
        this.colonyId = compound.getInteger(TAG_COLONY_ID);

        if (this.colonyId == -1 && worldObj != null)
        {
            final IColony colony = IColonyManager.getInstance().getIColony(worldObj, xCoord, yCoord, zCoord);
            if (colony != null)
            {
                this.colonyId = colony.getID();
            }
        }
    }

    /**
     * Get the banner patterns list.
     *
     * @return NBTTagList of banner patterns.
     */
    public NBTTagList getPatterns()
    {
        return patterns;
    }

    /**
     * Set the banner patterns list from a colony.
     *
     * @param colony the colony to get flag data from.
     */
    public void setFromColony(final IColony colony)
    {
        if (colony != null)
        {
            colonyId = colony.getID();
            // TODO: get colony flag patterns from colony data
            markDirty();
        }
    }
}
