package com.minecolonies.core.tileentities;

import com.minecolonies.api.tileentities.ITickable;
import com.minecolonies.api.util.WorldUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import java.util.Random;

import static com.minecolonies.api.util.constant.Constants.TICKS_SECOND;
import static com.minecolonies.api.util.constant.Constants.UPDATE_FLAG;

/**
 * The composted dirt tileEntity to grow all kinds of flowers.
 */
public class TileEntityCompostedDirt extends TileEntity implements ITickable
{
    /** If currently composted. */
    private boolean composted = false;

    /** The current tick timer. */
    private int ticker = 0;

    /** Chance to grow something (per second). */
    private double percentage = 1.0D;

    /** Max tick limit. */
    private static final int TICKER_LIMIT = 300;

    /** Random tick. */
    private final Random random = new Random();

    /** The flower to grow. */
    private ItemStack flower;

    public TileEntityCompostedDirt()
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

        if (!composted)
        {
            return;
        }

        ticker++;
        if (ticker >= TICKER_LIMIT)
        {
            ticker = 0;
            if (random.nextDouble() < percentage / TICKS_SECOND)
            {
                tryGrowFlower();
            }
        }
    }

    @Override
    public void updateEntity()
    {
        tick();
    }

    /**
     * Attempt to grow the flower above this block.
     */
    private void tryGrowFlower()
    {
        if (flower == null)
        {
            return;
        }
        // TODO: port flower growing logic to 1.7.10 (no DoublePlantBlock equivalent)
        // In 1.7.10: worldObj.setBlock(xCoord, yCoord + 1, zCoord, block, meta, UPDATE_FLAG)
    }

    /**
     * Set the flower to grow and percentage chance.
     *
     * @param flower     the flower ItemStack.
     * @param percentage grow chance.
     */
    public void setFlower(final ItemStack flower, final double percentage)
    {
        this.flower = flower;
        this.percentage = percentage;
        this.composted = flower != null;
        markDirty();
    }

    @Override
    public void writeToNBT(final NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setBoolean("composted", composted);
        compound.setInteger("ticker", ticker);
        compound.setDouble("percentage", percentage);
        if (flower != null)
        {
            final NBTTagCompound flowerTag = new NBTTagCompound();
            flower.writeToNBT(flowerTag);
            compound.setTag("flower", flowerTag);
        }
    }

    @Override
    public void readFromNBT(final NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        composted = compound.getBoolean("composted");
        ticker = compound.getInteger("ticker");
        percentage = compound.getDouble("percentage");
        if (compound.hasKey("flower"))
        {
            flower = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("flower"));
        }
    }

    @Override
    public void markDirty()
    {
        if (worldObj != null)
        {
            WorldUtil.markChunkDirty(worldObj, xCoord, yCoord, zCoord);
        }
    }

    public boolean isComposted() { return composted; }
    public ItemStack getFlower() { return flower; }
}
