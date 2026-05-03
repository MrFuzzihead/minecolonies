package com.minecolonies.core.network.messages.server.colony.building.warehouse;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.api.util.InventoryUtils;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingWareHouse;
import com.minecolonies.core.network.messages.server.AbstractBuildingServerMessage;
import net.minecraft.init.Blocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
// [1.7.10] items shim in com.minecolonies.api.shim

/**
 * Issues the upgrade of the warehouse pos World 5.
 */
public class UpgradeWarehouseMessage extends AbstractBuildingServerMessage<BuildingWareHouse>
{
    /**
     * Empty constructor used when registering the
     */
    public UpgradeWarehouseMessage()
    {
        super();
    }

    @Override
    protected void toBytesOverride(final PacketBuffer buf)
    {

    }

    @Override
    protected void fromBytesOverride(final PacketBuffer buf)
    {

    }

    public UpgradeWarehouseMessage(final IBuildingView building)
    {
        super(building);
    }

    @Override
    protected void onExecute(final MessageContext ctx, final boolean isLogicalServer, final IColony colony, final BuildingWareHouse building)
    {
        final Player player = ctx.getServerHandler().playerEntity;
        if (player == null)
        {
            return;
        }

        building.upgradeContainers(player.World);

        final boolean isCreative = player.isCreative();
        if (!isCreative)
        {
            final int slot = InventoryUtils.
                                             findFirstSlotInItemHandlerWith(new InvWrapper(player.getInventory()),
                                               itemStack -> ItemStack.isSameItem(itemStack, new ItemStack(Blocks.EMERALD_BLOCK)));
            player.getInventory().removeItem(slot, 1);
        }
    }
}



