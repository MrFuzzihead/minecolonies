package com.minecolonies.core.tileentities;

import com.google.common.collect.ImmutableList;
import com.minecolonies.api.blocks.AbstractBlockMinecoloniesRack;
import com.minecolonies.api.blocks.ModBlocks;
import com.minecolonies.api.blocks.types.RackType;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.tileentities.AbstractTileEntityColonyBuilding;
import com.minecolonies.api.tileentities.AbstractTileEntityRack;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.api.util.WorldUtil;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
// [1.7.10] items shim in com.minecolonies.api.shim
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

import static com.minecolonies.api.util.constant.Constants.*;
import static com.minecolonies.api.util.constant.NbtTagConstants.*;

/**
 * Tile entity for the warehouse shelves.
 */
public class TileEntityRack extends AbstractTileEntityRack
{
    /**
     * All Racks current version id.
     */
    private static final byte VERSION = 2;

    /**
     * The racks version.
     */
    private byte version = 0;

    /**
     * The content of the chest.
     */
    private final Object2IntMap<ItemStorage> content = new Object2IntOpenHashMap<>();

    /**
     * Size multiplier of the inventory. 0 = default value. 1 = 1*9 additional slots.
     */
    private int size = 0;

    /**
     * Amount of free slots.
     */
    private int freeSlots = 0;

    /**
     * If we did a double check after startup.
     */
    private boolean checkedAfterStartup = false;

    /**
     * Create a new rack.
     */
    public TileEntityRack()
    {
        super();
        this.freeSlots = inventory.getSlots();
    }

    /**
     * Create a rack with a specific inventory size.
     *
     * @param size the rack size.
     */
    public TileEntityRack(final int size)
    {
        super(size);
        this.size = ((size - DEFAULT_SIZE) / SLOT_PER_LINE);
        this.freeSlots = inventory.getSlots();
    }

    @Override
    public void setInWarehouse(final Boolean isInWarehouse)
    {
        this.inWarehouse = isInWarehouse;
    }

    @Override
    public int getFreeSlots()
    {
        return freeSlots;
    }

    @Override
    public boolean hasItemStack(final ItemStack stack, final int count, final boolean ignoreDamageValue)
    {
        final ItemStorage checkItem = new ItemStorage(stack, ignoreDamageValue);
        return content.getOrDefault(checkItem, 0) >= count;
    }

    @Override
    public boolean hasItemStorage(final ItemStorage storage, final int count)
    {
        return content.getOrDefault(storage, 0) >= count;
    }

    @Override
    public int getCount(final ItemStack stack, final boolean ignoreDamageValue, final boolean ignoreNBT)
    {
        final ItemStorage checkItem = new ItemStorage(stack, ignoreDamageValue, ignoreNBT);
        return getCount(checkItem);
    }

    @Override
    protected void updateBlockState()
    {
        // noop — block state updates handled via metadata in 1.7.10
    }

    @Override
    public int getCount(final ItemStorage storage)
    {
        if (storage.ignoreDamageValue() || storage.ignoreNBT())
        {
            if (!content.containsKey(storage))
            {
                return 0;
            }

            int count = 0;
            for (final Map.Entry<ItemStorage, Integer> contentStorage : content.entrySet())
            {
                if (contentStorage.getKey().equals(storage))
                {
                    count += contentStorage.getValue();
                }
            }
            return count;
        }

        return content.getOrDefault(storage, 0);
    }

    @Override
    public boolean hasItemStack(@NotNull final Predicate<ItemStack> itemStackSelectionPredicate)
    {
        for (final Map.Entry<ItemStorage, Integer> entry : content.entrySet())
        {
            if (itemStackSelectionPredicate.test(entry.getKey().getItemStack()))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasSimilarStack(@NotNull final ItemStack stack)
    {
        final ItemStorage checkItem = new ItemStorage(stack, true, true);
        if (content.containsKey(checkItem))
        {
            return true;
        }

        for (final ItemStorage storage : content.keySet())
        {
            if (IColonyManager.getInstance().getCompatibilityManager().getCreativeTab(checkItem) == IColonyManager.getInstance().getCompatibilityManager().getCreativeTab(storage))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the content of the Rack.
     *
     * @return the map of content.
     */
    public Map<ItemStorage, Integer> getAllContent()
    {
        return content;
    }

    @Override
    public void upgradeRackSize()
    {
        ++size;
        final RackInventory tempInventory = new RackInventory(DEFAULT_SIZE + size * SLOT_PER_LINE);
        for (int slot = 0; slot < inventory.getSlots(); slot++)
        {
            tempInventory.setStackInSlot(slot, inventory.getStackInSlot(slot));
        }

        inventory = tempInventory;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public int getItemCount(final Predicate<ItemStack> predicate)
    {
        int matched = 0;
        for (final Map.Entry<ItemStorage, Integer> entry : content.entrySet())
        {
            if (predicate.test(entry.getKey().getItemStack()))
            {
                matched += entry.getValue();
            }
        }
        return matched;
    }

    @Override
    public void updateItemStorage()
    {
        if (worldObj != null && !worldObj.isRemote)
        {
            final boolean beforeEmpty = content.isEmpty();
            updateContent();
            if (worldObj.getBlock(xCoord, yCoord, zCoord) == ModBlocks.blockRack)
            {
                boolean afterEmpty = content.isEmpty();
                final AbstractTileEntityRack potentialNeighbor = getOtherChest();
                if (potentialNeighbor instanceof TileEntityRack && !((TileEntityRack) potentialNeighbor).isEmpty())
                {
                    afterEmpty = false;
                }

                if ((beforeEmpty && !afterEmpty) || (!beforeEmpty && afterEmpty))
                {
                    // TODO: Update block appearance in 1.7.10 via metadata
                    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

                    if (potentialNeighbor != null)
                    {
                        worldObj.markBlockForUpdate(potentialNeighbor.xCoord, potentialNeighbor.yCoord, potentialNeighbor.zCoord);
                    }
                }
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
        freeSlots = 0;
        for (int slot = 0; slot < inventory.getSlots(); slot++)
        {
            final ItemStack stack = inventory.getStackInSlot(slot);

            if (ItemStackUtils.isEmpty(stack))
            {
                freeSlots++;
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
    public AbstractTileEntityRack getOtherChest()
    {
        if (worldObj.getBlock(xCoord, yCoord, zCoord) != ModBlocks.blockRack)
        {
            return null;
        }

        // TODO: In 1.7.10, determine the facing from block metadata and look up the neighbor
        // For now, return null — double-chest logic will be wired after block metadata porting
        return null;
    }

    @Override
    public ItemStackHandler createInventory(final int slots)
    {
        return new RackInventory(slots);
    }

    @Override
    public boolean isEmpty()
    {
        return content.isEmpty();
    }

    @Override
    public void readFromNBT(final NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        if (compound.hasKey(TAG_SIZE))
        {
            size = compound.getInteger(TAG_SIZE);
            inventory = createInventory(DEFAULT_SIZE + size * SLOT_PER_LINE);
        }

        final NBTTagList inventoryTagList = compound.getTagList(TAG_INVENTORY, 10); // 10 = NBTTagCompound
        for (int i = 0; i < inventoryTagList.tagCount(); i++)
        {
            final NBTTagCompound inventoryCompound = inventoryTagList.getCompoundTagAt(i);
            if (!inventoryCompound.hasKey(TAG_EMPTY))
            {
                final ItemStack stack = ItemStack.loadItemStackFromNBT(inventoryCompound);
                inventory.setStackInSlot(i, stack);
            }
        }

        updateContent();

        this.inWarehouse = compound.getBoolean(TAG_IN_WAREHOUSE);
        if (compound.hasKey(TAG_POS))
        {
            final int[] pos = BlockPosUtil.read(compound, TAG_POS);
            this.buildingPosX = pos[0];
            this.buildingPosY = pos[1];
            this.buildingPosZ = pos[2];
        }
        version = compound.getByte(TAG_VERSION);
    }

    @Override
    public void writeToNBT(final NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setInteger(TAG_SIZE, size);
        @NotNull final NBTTagList inventoryTagList = new NBTTagList();
        for (int slot = 0; slot < inventory.getSlots(); slot++)
        {
            @NotNull final NBTTagCompound inventoryCompound = new NBTTagCompound();
            final ItemStack stack = inventory.getStackInSlot(slot);
            if (ItemStackUtils.isEmpty(stack))
            {
                inventoryCompound.setBoolean(TAG_EMPTY, true);
            }
            else
            {
                stack.writeToNBT(inventoryCompound);
            }
            inventoryTagList.appendTag(inventoryCompound);
        }
        compound.setTag(TAG_INVENTORY, inventoryTagList);
        compound.setBoolean(TAG_IN_WAREHOUSE, inWarehouse);
        BlockPosUtil.write(compound, TAG_POS, buildingPosX, buildingPosY, buildingPosZ);
        compound.setByte(TAG_VERSION, version);
    }

    @Override
    public int getUpgradeSize()
    {
        return size;
    }

    @Override
    public void markDirty()
    {
        if (worldObj != null)
        {
            WorldUtil.markChunkDirty(worldObj, xCoord, yCoord, zCoord);
            super.markDirty();
        }
    }
}


