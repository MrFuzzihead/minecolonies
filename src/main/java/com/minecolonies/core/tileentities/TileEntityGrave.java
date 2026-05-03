package com.minecolonies.core.tileentities;

import com.minecolonies.api.blocks.AbstractBlockMinecoloniesGrave;
import com.minecolonies.api.blocks.types.GraveType;
import com.minecolonies.api.colony.GraveData;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.tileentities.AbstractTileEntityGrave;
import com.minecolonies.api.tileentities.AbstractTileEntityRack;
import com.minecolonies.api.util.InventoryUtils;
import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.api.util.WorldUtil;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
// [1.7.10] items shim in com.minecolonies.api.shim
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_DECAYED;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_DECAY_TIMER;

/**
 * Tile entity for the graves.
 */
public class TileEntityGrave extends AbstractTileEntityGrave
{
    /**
     * The content of the grave.
     */
    private final Map<ItemStorage, Integer> content = new HashMap<>();

    /**
     * NBTTag to store grave data.
     */
    private static final String TAG_GRAVE_DATA = "gravedata";

    public TileEntityGrave()
    {
        super();
    }

    /**
     * Gets the content of the grave.
     *
     * @return the map of content.
     */
    public Map<ItemStorage, Integer> getAllContent()
    {
        return content;
    }

    @Override
    public void updateItemStorage()
    {
        if (worldObj != null && !worldObj.isRemote)
        {
            final boolean empty = content.isEmpty();
            updateContent();

            if ((empty && !content.isEmpty()) || (!empty && content.isEmpty()))
            {
                updateBlockState();
            }
            markDirty();
        }
    }

    /**
     * Just do the content update.
     */
    private void updateContent()
    {
        content.clear();
        for (int slot = 0; slot < inventory.getSlots(); slot++)
        {
            final ItemStack stack = inventory.getStackInSlot(slot);

            if (ItemStackUtils.isEmpty(stack))
            {
                continue;
            }

            final ItemStorage storage = new ItemStorage(stack.copy());
            int amount = ItemStackUtils.getSize(stack);
            if (content.containsKey(storage))
            {
                amount += content.remove(storage);
            }
            content.put(storage, amount);
        }
    }

    @Override
    public void updateBlockState()
    {
        if (worldObj != null && worldObj.getBlock(xCoord, yCoord, zCoord) instanceof AbstractBlockMinecoloniesGrave)
        {
            // TODO: Update block metadata for decayed/default state in 1.7.10
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public ItemStackHandler createInventory(final int slots)
    {
        return new AbstractTileEntityRack.RackInventory(slots);
    }

    @Override
    public boolean isEmpty()
    {
        updateContent();
        return content.isEmpty();
    }

    @Override
    public void readFromNBT(final NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        decay_timer = compound.hasKey(TAG_DECAY_TIMER) ? compound.getInteger(TAG_DECAY_TIMER) : DEFAULT_DECAY_TIMER;
        decayed     = compound.hasKey(TAG_DECAYED) && compound.getBoolean(TAG_DECAYED);

        if (compound.hasKey(TAG_GRAVE_DATA))
        {
            graveData = new GraveData();
            graveData.read(compound.getCompoundTag(TAG_GRAVE_DATA));
        }
        else
        {
            graveData = null;
        }
    }

    @Override
    public void writeToNBT(final NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setInteger(TAG_DECAY_TIMER, decay_timer);
        compound.setBoolean(TAG_DECAYED, decayed);

        if (graveData != null)
        {
            compound.setTag(TAG_GRAVE_DATA, graveData.write());
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

    /**
     * Update the decay of this grave on colony tick.
     *
     * @param delay number of ticks between each call.
     * @return true if the grave still exists, false otherwise.
     */
    public boolean onColonyTick(final double delay)
    {
        if (worldObj != null && !worldObj.isRemote && decay_timer != -1)
        {
            decay_timer -= delay;
            if (decay_timer <= 0)
            {
                if (!decayed)
                {
                    decayed = true;
                    decay_timer = DEFAULT_DECAY_TIMER;
                    updateBlockState();
                }
                else
                {
                    InventoryUtils.dropItemHandler(inventory, worldObj, xCoord, yCoord, zCoord);
                    worldObj.setBlock(xCoord, yCoord, zCoord, Blocks.air, 0, 3);
                    return false;
                }
            }
        }
        return true;
    }
}

