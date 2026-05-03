package com.minecolonies.core.network.messages.client.colony;

import com.minecolonies.api.network.IMessage;
import com.minecolonies.core.datalistener.DiseasesListener;
// [1.7.10] client removed (use @SideOnly)
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The message used to synchronize global disease data from a server to a remote client.
 */
public class GlobalDiseaseSyncMessage implements IMessage
{
    /**
     * The buffer with the data.
     */
    private PacketBuffer buffer;

    /**
     * Empty constructor used when registering the message
     */
    public GlobalDiseaseSyncMessage()
    {
        super();
    }

    /**
     * Add or Update expedition type data on the client.
     *
     * @param buf the bytebuffer.
     */
    public GlobalDiseaseSyncMessage(final PacketBuffer buf)
    {
        this.buffer = new PacketBuffer(buf.copy());
    }

    @Override
    public void toBytes(@NotNull final PacketBuffer buf)
    {
        buffer.resetReaderIndex();
        buf.writeBytes(buffer);
    }

    @Override
    public void fromBytes(@NotNull final PacketBuffer buf)
    {
        buffer = new PacketBuffer(buf.retain());
    }

    @Nullable
    @Override
    public Boolean getExecutionSide()
    {
        return Boolean.FALSE;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onExecute(final MessageContext ctx, final boolean isLogicalServer)
    {
        if (Minecraft.getInstance().World != null)
        {
            DiseasesListener.readGlobalDiseasesPackets(buffer);
        }
        buffer.release();
    }
}



