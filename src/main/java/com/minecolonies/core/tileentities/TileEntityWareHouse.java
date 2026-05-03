package com.minecolonies.core.tileentities;

import com.minecolonies.api.inventory.InventoryCitizen;
import com.minecolonies.api.tileentities.AbstractTileEntityRack;
import com.minecolonies.api.tileentities.AbstractTileEntityWareHouse;
import com.minecolonies.api.util.*;
import com.minecolonies.core.colony.buildings.modules.BuildingModules;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static com.minecolonies.api.util.constant.Constants.TICKS_FIVE_MIN;
import static com.minecolonies.api.util.constant.TranslationConstants.*;
import static com.minecolonies.core.colony.buildings.workerbuildings.BuildingWareHouse.MAX_STORAGE_UPGRADE;

/**
 * Class which handles the tileEntity of our colony warehouse.
 */
public class TileEntityWareHouse extends AbstractTileEntityWareHouse
{
    /**
     * Time of last sent notifications (in world time).
     */
    private long lastNotification = 0;

    public TileEntityWareHouse()
    {
        super();
        inWarehouse = true;
    }

    @Override
    public boolean hasMatchingItemStackInWarehouse(@NotNull final Predicate<ItemStack> itemStackSelectionPredicate, final int count)
    {
        int totalCount = 0;
        if (getBuilding() != null)
        {
            for (@NotNull final int[] pos : getBuilding().getContainers())
            {
                if (WorldUtil.isBlockLoaded(worldObj, pos[0], pos[2]))
                {
                    final TileEntity entity = worldObj.getTileEntity(pos[0], pos[1], pos[2]);
                    if (entity instanceof TileEntityRack && !((TileEntityRack) entity).isEmpty())
                    {
                        totalCount += ((TileEntityRack) entity).getItemCount(itemStackSelectionPredicate);
                        if (totalCount >= count)
                        {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean hasMatchingItemStackInWarehouse(@NotNull final ItemStack itemStack, final int count, final boolean ignoreNBT)
    {
        return hasMatchingItemStackInWarehouse(itemStack, count, ignoreNBT, 0);
    }

    @Override
    public boolean hasMatchingItemStackInWarehouse(@NotNull final ItemStack itemStack, final int count, final boolean ignoreNBT, final boolean ignoreDamage, final int leftOver)
    {
        int totalCountFound = 0 - leftOver;
        if (getBuilding() != null)
        {
            for (@NotNull final int[] pos : getBuilding().getContainers())
            {
                if (WorldUtil.isBlockLoaded(worldObj, pos[0], pos[2]))
                {
                    final TileEntity entity = worldObj.getTileEntity(pos[0], pos[1], pos[2]);
                    if (entity instanceof AbstractTileEntityRack && !((AbstractTileEntityRack) entity).isEmpty())
                    {
                        totalCountFound += ((AbstractTileEntityRack) entity).getCount(itemStack, ignoreDamage, ignoreNBT);
                        if (totalCountFound >= count)
                        {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean hasMatchingItemStackInWarehouse(@NotNull final ItemStack itemStack, final int count, final boolean ignoreNBT, final int leftOver)
    {
        return hasMatchingItemStackInWarehouse(itemStack, count, ignoreNBT, true, leftOver);
    }

    @Override
    @NotNull
    public List<Tuple<ItemStack, int[]>> getMatchingItemStacksInWarehouse(@NotNull final Predicate<ItemStack> itemStackSelectionPredicate)
    {
        final List<Tuple<ItemStack, int[]>> found = new ArrayList<>();

        if (getBuilding() != null)
        {
            for (@NotNull final int[] pos : getBuilding().getContainers())
            {
                if (WorldUtil.isBlockLoaded(worldObj, pos[0], pos[2]))
                {
                    final TileEntity entity = worldObj.getTileEntity(pos[0], pos[1], pos[2]);
                    if (entity instanceof TileEntityRack && !((TileEntityRack) entity).isEmpty()
                          && ((TileEntityRack) entity).getItemCount(itemStackSelectionPredicate) > 0)
                    {
                        for (final ItemStack stack : InventoryUtils.filterItemHandler(((TileEntityRack) entity).getInventory(), itemStackSelectionPredicate))
                        {
                            found.add(new Tuple<>(stack, pos));
                        }
                    }
                }
            }
        }
        return found;
    }

    @Override
    public void dumpInventoryIntoWareHouse(@NotNull final InventoryCitizen inventoryCitizen)
    {
        for (int i = 0; i < inventoryCitizen.getSlots(); i++)
        {
            final ItemStack stack = inventoryCitizen.getStackInSlot(i);
            if (ItemStackUtils.isEmpty(stack))
            {
                continue;
            }

            @Nullable final AbstractTileEntityRack chest = getRackForStack(stack);
            if (chest == null)
            {
                if (worldObj.getTotalWorldTime() - lastNotification > TICKS_FIVE_MIN)
                {
                    lastNotification = worldObj.getTotalWorldTime();
                    if (getBuilding() != null)
                    {
                        if (getBuilding().getBuildingLevel() == getBuilding().getMaxBuildingLevel())
                        {
                            if (getBuilding().getModule(BuildingModules.WAREHOUSE_OPTIONS).getStorageUpgrade() < MAX_STORAGE_UPGRADE)
                            {
                                MessageUtils.format(COM_MINECOLONIES_COREMOD_WAREHOUSE_FULL_LEVEL5_UPGRADE).sendTo(getColony()).forAllPlayers();
                            }
                            else
                            {
                                MessageUtils.format(COM_MINECOLONIES_COREMOD_WAREHOUSE_FULL_MAX_UPGRADE).sendTo(getColony()).forAllPlayers();
                            }
                        }
                        else
                        {
                            MessageUtils.format(COM_MINECOLONIES_COREMOD_WAREHOUSE_FULL).sendTo(getColony()).forAllPlayers();
                        }
                    }
                }
                return;
            }

            final int index = i;
            InventoryUtils.transferItemStackIntoNextBestSlotInItemHandler(inventoryCitizen, index, chest.getInventory());
        }
    }

    /**
     * Get a rack for a stack.
     *
     * @param stack the stack to insert.
     * @return the matching rack TE.
     */
    @Nullable
    public AbstractTileEntityRack getRackForStack(final ItemStack stack)
    {
        AbstractTileEntityRack rack = getPositionOfChestWithItemStack(stack);
        if (rack == null)
        {
            rack = getPositionOfChestWithSimilarItemStack(stack);
            if (rack == null)
            {
                rack = searchMostEmptyRack();
            }
        }
        return rack;
    }

    @Nullable
    private AbstractTileEntityRack getPositionOfChestWithItemStack(@NotNull final ItemStack stack)
    {
        if (getBuilding() == null)
        {
            return null;
        }
        for (@NotNull final int[] pos : getBuilding().getContainers())
        {
            if (WorldUtil.isBlockLoaded(worldObj, pos[0], pos[2]))
            {
                final TileEntity entity = worldObj.getTileEntity(pos[0], pos[1], pos[2]);
                if (entity instanceof AbstractTileEntityRack
                      && ((AbstractTileEntityRack) entity).getFreeSlots() > 0
                      && ((AbstractTileEntityRack) entity).hasItemStack(stack, 1, true))
                {
                    return (AbstractTileEntityRack) entity;
                }
            }
        }
        return null;
    }

    @Nullable
    private AbstractTileEntityRack getPositionOfChestWithSimilarItemStack(final ItemStack stack)
    {
        if (getBuilding() == null)
        {
            return null;
        }
        for (@NotNull final int[] pos : getBuilding().getContainers())
        {
            if (WorldUtil.isBlockLoaded(worldObj, pos[0], pos[2]))
            {
                final TileEntity entity = worldObj.getTileEntity(pos[0], pos[1], pos[2]);
                if (entity instanceof AbstractTileEntityRack
                      && ((AbstractTileEntityRack) entity).getFreeSlots() > 0
                      && ((AbstractTileEntityRack) entity).hasSimilarStack(stack))
                {
                    return (AbstractTileEntityRack) entity;
                }
            }
        }
        return null;
    }

    @Nullable
    private AbstractTileEntityRack searchMostEmptyRack()
    {
        int freeSlots = 0;
        AbstractTileEntityRack emptiestChest = null;
        if (getBuilding() == null)
        {
            return null;
        }
        for (@NotNull final int[] pos : getBuilding().getContainers())
        {
            final TileEntity entity = worldObj.getTileEntity(pos[0], pos[1], pos[2]);
            if (entity instanceof AbstractTileEntityRack)
            {
                if (((AbstractTileEntityRack) entity).isEmpty())
                {
                    return (AbstractTileEntityRack) entity;
                }

                final int tempFreeSlots = ((AbstractTileEntityRack) entity).getFreeSlots();
                if (tempFreeSlots > freeSlots)
                {
                    freeSlots = tempFreeSlots;
                    emptiestChest = (AbstractTileEntityRack) entity;
                }
            }
        }
        return emptiestChest;
    }
}
