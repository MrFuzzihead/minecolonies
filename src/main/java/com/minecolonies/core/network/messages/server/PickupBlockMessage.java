package com.minecolonies.core.network.messages.server;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.network.IMessage;
import com.minecolonies.api.util.InventoryUtils;
import com.minecolonies.api.util.MessageUtils;
import com.minecolonies.core.colony.Colony;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static com.minecolonies.api.util.constant.TranslationConstants.WARNING_BUILDING_PICKUP_PLAYER_INVENTORY_FULL;

/**
 * Pickup the town hall block.
 */
public class PickupBlockMessage implements IMessage
{
    /**
     * Position the player wants to found the colony at.
     */
    int[] pos;

    public PickupBlockMessage()
    {
        super();
    }

    public PickupBlockMessage(final int[] pos)
    {
        this.pos = pos;
    }

    @Override
    public void toBytes(final PacketBuffer buf)
    {
        buf.writeBlockPos(pos);
    }

    @Override
    public void fromBytes(final PacketBuffer buf)
    {
        pos = buf.readBlockPos();
    }

    @Nullable
    @Override
    public Boolean getExecutionSide()
    {
        return Boolean.TRUE;
    }

    @Override
    public void onExecute(final MessageContext ctx, final boolean isLogicalServer)
    {
        final EntityPlayerMP sender = ctx.getServerHandler().playerEntity;
        final World world = ctx.getServerHandler().playerEntity.World;

        if (sender == null)
        {
            return;
        }

        if (IColonyManager.getInstance().getColonyByPosFromWorld(world, pos) instanceof Colony)
        {
            return;
        }

        final ItemStack stack = new ItemStack(world.getBlockState(pos).getBlock(), 1);
        final NBTTagCompound compoundNBT = new NBTTagCompound();
        stack.setTag(compoundNBT);
        if (InventoryUtils.addItemStackToProvider(sender, stack))
        {
            world.destroyBlock(pos, false);
        }
        else
        {
            MessageUtils.format(WARNING_BUILDING_PICKUP_PLAYER_INVENTORY_FULL).sendTo(sender);
        }

    }
}




