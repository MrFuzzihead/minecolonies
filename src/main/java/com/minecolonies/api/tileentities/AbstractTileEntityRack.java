package com.minecolonies.api.tileentities;
import net.minecraft.world.level.block.state.BlockState;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.requestsystem.requestable.IDeliverable;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.util.ItemStackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

import static com.minecolonies.api.util.constant.Constants.DEFAULT_SIZE;

public abstract class AbstractTileEntityRack extends TileEntity
{
    /**
     * Whether this rack is in a warehouse or not.
     */
    protected boolean inWarehouse = false;

    /**
     * Pos of the owning building (encoded as x<<16|y<<8|z).
     * Stored as 3 separate ints to replace int[].
     */
    protected int buildingPosX = 0;
    protected int buildingPosY = 0;
    protected int buildingPosZ = 0;

    /**
     * The inventory of the tileEntity.
     */
    protected ItemStackHandler inventory;

    /**
     * Create a new rack.
     */
    public AbstractTileEntityRack()
    {
        super();
        inventory = createInventory(DEFAULT_SIZE);
    }

    /**
     * Create a rack with a specific inventory size.
     *
     * @param size the rack size.
     */
    public AbstractTileEntityRack(final int size)
    {
        super();
        inventory = createInventory(size);
    }

    /**
     * Rack inventory type.
     */
    public class RackInventory extends ItemStackHandler
    {
        public RackInventory(final int defaultSize)
        {
            super(defaultSize);
        }

        @Override
        protected void onContentsChanged(final int slot)
        {
            updateItemStorage();
            super.onContentsChanged(slot);
        }

        @Override
        public void setStackInSlot(final int slot, final @Nonnull ItemStack stack)
        {
            final ItemStack existing = stacks[slot];
            final boolean changed = !net.minecraft.item.ItemStack.areItemStackTagsEqual(stack, existing);
            stacks[slot] = stack;
            if (changed)
            {
                onContentsChanged(slot);
            }
            updateWarehouseIfAvailable(stack);
        }

        @Nonnull
        @Override
        public ItemStack insertItem(final int slot, @Nonnull final ItemStack stack, final boolean simulate)
        {
            final ItemStack result = super.insertItem(slot, stack, simulate);
            if ((result == null || result.stackSize < stack.stackSize) && !simulate)
            {
                updateWarehouseIfAvailable(stack);
            }
            return result;
        }
    }

    /**
     * Create the inventory that belongs to the rack.
     *
     * @param slots the number of slots.
     * @return the created inventory.
     */
    public abstract ItemStackHandler createInventory(final int slots);

    /**
     * Update the warehouse if available with the updated stack.
     *
     * @param stack the incoming stack.
     */
    public void updateWarehouseIfAvailable(final ItemStack stack)
    {
        if (!ItemStackUtils.isEmpty(stack) && worldObj != null && !worldObj.isRemote)
        {
            if (inWarehouse || (buildingPosX != 0 || buildingPosY != 0 || buildingPosZ != 0))
            {
                if (IColonyManager.getInstance().isCoordinateInAnyColony(worldObj, new int[]{xCoord, yCoord, zCoord}))
                {
                    final IColony colony = IColonyManager.getInstance().getClosestColony(worldObj, new int[]{xCoord, yCoord, zCoord});
                    if (colony == null)
                    {
                        return;
                    }

                    if (inWarehouse)
                    {
                        colony.getRequestManager().onColonyUpdate(request ->
                          request.getRequest() instanceof IDeliverable && ((IDeliverable) request.getRequest()).matches(stack));
                    }
                    else
                    {
                        final IBuilding building = colony.getServerBuildingManager().getBuilding(new int[]{buildingPosX, buildingPosY, buildingPosZ});
                        if (building != null)
                        {
                            building.overruleNextOpenRequestWithStack(stack);
                            building.markDirty();
                        }
                    }
                }
            }
        }
    }

    /**
     * Set the value for inWarehouse.
     *
     * @param isInWarehouse is this rack in a warehouse?
     */
    public abstract void setInWarehouse(Boolean isInWarehouse);

    /**
     * Get the amount of free slots in the inventory.
     *
     * @return the amount of free slots.
     */
    public abstract int getFreeSlots();

    /**
     * Check if a similar/same item as the stack is in the inventory.
     *
     * @param stack             the stack to check.
     * @param count             the min count it should have.
     * @param ignoreDamageValue ignore the damage value.
     * @return true if so.
     */
    public abstract boolean hasItemStack(ItemStack stack, final int count, boolean ignoreDamageValue);

    /**
     * Check if the itemStorage exists in the inventory.
     *
     * @param storage the storage to check.
     * @param count   the min count it should have.
     * @return true if so.
     */
    public abstract boolean hasItemStorage(final ItemStorage storage, final int count);

    /**
     * Check if a similar/same item as the stack is in the inventory and return the count.
     *
     * @param stack             the stack to check.
     * @param ignoreDamageValue ignore the damage value.
     * @param ignoreNBT         if nbt should be ignored.
     * @return the quantity or 0.
     */
    public abstract int getCount(ItemStack stack, boolean ignoreDamageValue, final boolean ignoreNBT);

    /**
     * Check if a similar/same item as the storage is in the inventory and return the count.
     *
     * @param storage the storage to match.
     * @return the quantity or 0.
     */
    public abstract int getCount(ItemStorage storage);

    /**
     * Check if a similar/same item as the stack is in the inventory.
     *
     * @param itemStackSelectionPredicate the predicate to test the stack against.
     * @return true if so.
     */
    public abstract boolean hasItemStack(@NotNull Predicate<ItemStack> itemStackSelectionPredicate);

    /**
     * Check if a similar stack is in the rack.
     *
     * @param stack stack to check.
     * @return a set of different results depending on the similarity metric.
     */
    public abstract boolean hasSimilarStack(@NotNull ItemStack stack);

    /**
     * Upgrade the rack by 1. This adds 9 more slots and copies the inventory to the new one.
     */
    public abstract void upgradeRackSize();

    /**
     * Set the building pos it belongs to.
     *
     * @param posX building x.
     * @param posY building y.
     * @param posZ building z.
     */
    public void setBuildingPos(final int posX, final int posY, final int posZ)
    {
        if (worldObj != null && (buildingPosX != posX || buildingPosY != posY || buildingPosZ != posZ))
        {
            markDirty();
        }
        this.buildingPosX = posX;
        this.buildingPosY = posY;
        this.buildingPosZ = posZ;
    }

    /**
     * Get the upgrade size.
     *
     * @return the upgrade size.
     */
    public abstract int getUpgradeSize();

    /**
     * Get the amount of items matching a predicate in the inventory.
     *
     * @param predicate the predicate.
     * @return the total count.
     */
    public abstract int getItemCount(Predicate<ItemStack> predicate);

    /**
     * Scans through the whole storage and updates it.
     */
    public abstract void updateItemStorage();

    /**
     * Update the blockState of the rack.
     */
    protected abstract void updateBlockState();

    /**
     * Get the other double chest or null.
     *
     * @return the tileEntity of the other half or null.
     */
    public abstract AbstractTileEntityRack getOtherChest();

    /**
     * Checks if the chest is empty.
     *
     * @return true if so.
     */
    public abstract boolean isEmpty();

    public IItemHandlerModifiable getInventory()
    {
        return inventory;
    }
}



