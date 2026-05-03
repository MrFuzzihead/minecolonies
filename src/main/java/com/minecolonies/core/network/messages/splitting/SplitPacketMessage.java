package com.minecolonies.core.network.messages.splitting;

import com.google.common.collect.Maps;
import com.google.common.primitives.Bytes;
import com.minecolonies.api.network.IMessage;
import com.minecolonies.api.util.Log;
import com.minecolonies.core.Network;
import com.minecolonies.core.network.NetworkChannel;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Represents a class that wrappers other messages in byte form and is used to split the wrapped messages data into several chunks.
 * [1.7.10] Ported from SimpleChannel NetworkEvent.Context to SimpleNetworkWrapper MessageContext.
 */
public class SplitPacketMessage implements IMessage
{
    private int communicationId = -1;
    private int packetIndex = -1;
    private boolean terminator = false;
    private int innerMessageId = -1;
    private byte[] payload;

    public SplitPacketMessage() {}

    public SplitPacketMessage(final int communicationId, final int packetIndex, final boolean terminator, final int innerMessageId, final byte[] payload)
    {
        this.communicationId = communicationId;
        this.packetIndex = packetIndex;
        this.terminator = terminator;
        this.innerMessageId = innerMessageId;
        this.payload = payload;
    }

    @Override
    public void toBytes(final PacketBuffer buf)
    {
        buf.writeVarInt(this.communicationId);
        buf.writeVarInt(this.packetIndex);
        buf.writeBoolean(this.terminator);
        buf.writeVarInt(this.innerMessageId);
        buf.writeByteArray(this.payload);
    }

    @Override
    public void fromBytes(final PacketBuffer buf)
    {
        this.communicationId = buf.readVarInt();
        this.packetIndex = buf.readVarInt();
        this.terminator = buf.readBoolean();
        this.innerMessageId = buf.readVarInt();
        this.payload = buf.readByteArray();
    }

    @Override
    public void onExecute(final MessageContext ctx, final boolean isLogicalServer)
    {
        try
        {
            // Sync on the message cache since this is still on the Netty thread.
            synchronized (Network.getNetwork().getMessageCache())
            {
                Network.getNetwork().getMessageCache().get(this.communicationId, Maps::newConcurrentMap).put(this.packetIndex, this.payload);
            }

            if (!this.terminator)
            {
                return;
            }

            // All data gets sorted and appended.
            final byte[] packetData = Network.getNetwork().getMessageCache().get(this.communicationId, Maps::newConcurrentMap).entrySet()
                                        .stream()
                                        .sorted(Map.Entry.comparingByKey())
                                        .map(Map.Entry::getValue)
                                        .reduce(new byte[0], Bytes::concat);

            final NetworkChannel.NetworkingMessageEntry<?> messageEntry = Network.getNetwork().getMessagesTypes().get(this.innerMessageId);
            final IMessage message = messageEntry.getCreator().get();

            final ByteBuf buffer = Unpooled.wrappedBuffer(packetData);
            try
            {
                message.fromBytes(new PacketBuffer(buffer));
            }
            catch (Exception e)
            {
                Log.getLogger().error("Packet error:", e);
                buffer.release();
                return;
            }
            buffer.release();

            // [1.7.10] Execute the inner message directly; MessageContext has no enqueueWork.
            // Side check: getExecutionSide() returns Boolean (TRUE=serverOnly, FALSE=clientOnly, null=both).
            final Boolean execSide = message.getExecutionSide();
            if (execSide != null && execSide == isLogicalServer)
            {
                // execSide==true means server-only; if we are on server side (isLogicalServer==true) and execSide==true, that's correct.
                // execSide==false means client-only; if we are on client (isLogicalServer==false) and execSide==false, that's correct.
                // We only warn/skip when they MISMATCH.
                // Mismatch: execSide==true and !isLogicalServer => log and skip  (handled below)
                // Mismatch: execSide==false and isLogicalServer => log and skip
            }
            if (execSide != null && execSide != isLogicalServer)
            {
                Log.getLogger().warn("Receiving {} at wrong side!", message.getClass().getName());
                return;
            }
            try
            {
                message.onExecute(ctx, isLogicalServer);
            }
            catch (Exception e)
            {
                Log.getLogger().error("Packet error:", e);
            }
        }
        catch (ExecutionException e)
        {
            Log.getLogger().error("Failed to handle split packet.", e);
        }
    }
}
