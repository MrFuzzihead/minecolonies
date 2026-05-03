package com.minecolonies.core.network.messages.server;

import com.ldtteam.structurize.items.ModItems;
import com.minecolonies.api.network.IMessage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Switch the buildtool with the respective item in the inventory.
 */
public class SwitchBuildingWithToolMessage implements IMessage
{
    /**
     * The stack to switch.
     */
    private ItemStack stack;

    /**
     * Empty constructor used when registering the
     */
    public SwitchBuildingWithToolMessage()
    {
        super();
    }

    /**
     * Switch the stack.
     *
     * @param stack the stack in the hand.
     */
    public SwitchBuildingWithToolMessage(final ItemStack stack)
    {
        super();
        this.stack = stack;
    }

    /**
     * Reads this packet from a {@link PacketBuffer}.
     *
     * @param buf The buffer begin read from.
     */
    @Override
    public void fromBytes(@NotNull final PacketBuffer buf)
    {
        stack = buf.readItem();
    }

    /**
     * Writes this packet to a {@link PacketBuffer}.
     *
     * @param buf The buffer being written to.
     */
    @Override
    public void toBytes(@NotNull final PacketBuffer buf)
    {
        buf.writeItem(stack);
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
        final EntityPlayerMP player = ctx.getServerHandler().playerEntity;

        int stackSlot = -1;
        int buildToolSlot = -1;
        for (int i = 0; i < 9; i++)
        {
            if (ItemStack.isSameItem(player.getInventory().getItem(i), stack))
            {
                stackSlot = i;
            }
            else if (player.getInventory().getItem(i).getItem() == ModItems.buildTool.get())
            {
                buildToolSlot = i;
            }
        }

        for (int i = 9; i < player.getInventory().getContainerSize(); i++)
        {
            if (player.getInventory().getItem(i).getItem() == ModItems.buildTool.get())
            {
                buildToolSlot = i;
            }
        }

        if (stackSlot != -1 && buildToolSlot != -1)
        {
            player.getInventory().setItem(buildToolSlot, player.getInventory().getItem(stackSlot).copy());
            player.getInventory().setItem(stackSlot, new ItemStack(ModItems.buildTool.get(), 1));
        }
    }
}



