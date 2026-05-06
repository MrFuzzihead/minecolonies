package com.minecolonies.core.network.messages.server.colony;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.util.InventoryUtils;
import com.minecolonies.core.network.messages.server.AbstractColonyServerMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
// [1.7.10] items shim in com.minecolonies.api.shim

import static com.minecolonies.core.colony.buildings.workerbuildings.BuildingBarracks.SPIES_GOLD_COST;

/**
 * Message for hiring spies at the cost of gold.
 */
public class HireSpiesMessage extends AbstractColonyServerMessage
{
    public HireSpiesMessage()
    {
    }

    public HireSpiesMessage(final IColony colony)
    {
        super(colony);
    }

    @Override
    protected void onExecute(final MessageContext ctx, final boolean isLogicalServer, final IColony colony)
    {
        final Player player = ctx.getServerHandler().playerEntity;
        if (player == null)
        {
            return;
        }

        if (InventoryUtils.getItemCountInItemHandler(new InvWrapper(player.getInventory()), stack -> stack.getItem() == Items.GOLD_INGOT) >= SPIES_GOLD_COST)
        {
            InventoryUtils.reduceStackInItemHandler(new InvWrapper(player.getInventory()), new ItemStack(Items.GOLD_INGOT), SPIES_GOLD_COST);
            colony.getRaiderManager().setSpiesEnabled(true);
            colony.markDirty();
        }
    }

    @Override
    protected void toBytesOverride(final PacketBuffer buf)
    {


    }

    @Override
    protected void fromBytesOverride(final PacketBuffer buf)
    {

    }
}



