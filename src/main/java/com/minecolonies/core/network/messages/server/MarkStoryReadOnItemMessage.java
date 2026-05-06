package com.minecolonies.core.network.messages.server;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.items.ISupplyItem;
import com.minecolonies.api.network.IMessage;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayerMP;
// [1.7.10] int /* InteractionHand */ removed
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_SAW_STORY;

/**
 * Mark that for a given item the story was already read.
 */
public class MarkStoryReadOnItemMessage implements IMessage
{
    /**
     * The hand that was holding the item.
     */
    private int /* InteractionHand */ hand;

    /**
     * Empty constructor used when registering the message
     */
    public MarkStoryReadOnItemMessage()
    {
        super();
    }

    /**
     * Set it on the item on the server side.
     *
     * @param hand the hand with the item.
     */
    public MarkStoryReadOnItemMessage(final int /* InteractionHand */ hand)
    {
        super();
        this.hand = hand;
    }

    @Override
    public void fromBytes(@NotNull final PacketBuffer buf)
    {
        hand = buf.readInt(); // [1.7.10] InteractionHand removed - 0=main, 1=off
    }

    @Override
    public void toBytes(@NotNull final PacketBuffer buf)
    {
        buf.writeInt(hand.ordinal());
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
        final ItemStack stackInHand = player.getItemInHand(this.hand);
        if (stackInHand.getItem() instanceof ISupplyItem)
        {
            stackInHand.getOrCreateTag().putBoolean(TAG_SAW_STORY, true);
        }
    }
}



