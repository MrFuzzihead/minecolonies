package com.minecolonies.api.network;

import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import javax.annotation.Nullable;

/**
 * Interface for all network messages.
 * [1.7.10] Ported from 1.21 SimpleChannel to SimpleNetworkWrapper pattern.
 * onExecute receives a MessageContext instead of NetworkEvent.Context.
 */
public interface IMessage extends cpw.mods.fml.common.network.simpleimpl.IMessage
{
    /**
     * Writes message data to buffer.
     *
     * @param buf network data byte buffer
     */
    void toBytes(final PacketBuffer buf);

    /**
     * Reads message data from buffer.
     *
     * @param buf network data byte buffer
     */
    void fromBytes(final PacketBuffer buf);

    /**
     * [1.7.10] Bridge: fromBytes(ByteBuf) delegates to fromBytes(PacketBuffer).
     */
    @Override
    default void fromBytes(final io.netty.buffer.ByteBuf buf)
    {
        fromBytes(new PacketBuffer(buf));
    }

    /**
     * [1.7.10] Bridge: toBytes(ByteBuf) delegates to toBytes(PacketBuffer).
     */
    @Override
    default void toBytes(final io.netty.buffer.ByteBuf buf)
    {
        toBytes(new PacketBuffer(buf));
    }

    /**
     * Which side is this message executed on.
     * [1.7.10] Replaces Boolean; return true if server-side only, false if client-side only, null for both.
     * @return null (both), or Boolean: TRUE = server only, FALSE = client only
     */
    @Nullable
    default Boolean getExecutionSide()
    {
        return null; // both sides
    }

    /**
     * Executes message action.
     * [1.7.10] Uses MessageContext from SimpleNetworkWrapper instead of NetworkEvent.Context.
     *
     * @param ctx             network context of incoming message
     * @param isLogicalServer whether message arrived at logical server side
     */
    void onExecute(final MessageContext ctx, final boolean isLogicalServer);
}
