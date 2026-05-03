package com.minecolonies.core.tileentities;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.util.MessageUtils;
// [1.7.10] items shim in com.minecolonies.api.shim
import net.minecraftforge.items.ItemStackHandler;

import static com.minecolonies.api.colony.requestsystem.requestable.deliveryman.AbstractDeliverymanRequestable.getPlayerActionPriority;
import static com.minecolonies.api.util.constant.TranslationConstants.COM_MINECOLONIES_COREMOD_ENTITY_DELIVERYMAN_FORCEPICKUP;

/**
 * Class which handles the tileEntity for the Stash block.
 */
public class TileEntityStash extends TileEntityColonyBuilding
{
    /**
     * Default constructor.
     */
    public TileEntityStash()
    {
        super();
    }

    @Override
    public ItemStackHandler createInventory(final int slots)
    {
        return new NotifyingRackInventory(slots);
    }

    /**
     * An {@link ItemStackHandler} that notifies the container TileEntity when it's inventory has changed.
     */
    public class NotifyingRackInventory extends RackInventory
    {
        public NotifyingRackInventory(final int defaultSize)
        {
            super(defaultSize);
        }

        @Override
        protected void onContentsChanged(final int slot)
        {
            super.onContentsChanged(slot);
            onInventoryChanged();
        }
    }

    /**
     * Called when the stash inventory changes.
     */
    private void onInventoryChanged()
    {
        if (worldObj == null || worldObj.isRemote)
        {
            return;
        }

        final IColony colony = getColony();
        if (colony == null)
        {
            return;
        }

        final IBuilding building = colony.getServerBuildingManager().getBuilding(xCoord, yCoord, zCoord);
        if (building == null)
        {
            return;
        }

        if (!isEmpty())
        {
            colony.getDeliveryManager().createPickupRequest(building, getPlayerActionPriority(true));
        }
    }
}

