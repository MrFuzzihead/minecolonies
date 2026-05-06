package com.minecolonies.core.network.messages.client;
import net.minecraft.client.multiplayer.ClientLevel;

import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.network.IMessage;
import com.minecolonies.api.util.Log;
import io.netty.buffer.Unpooled;
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Message to update the recipes on the client side.
 */
public class UpdateClientWithCompatibilityMessage implements IMessage
{
    private PacketBuffer buffer;

    /**
     * Empty public constructor.
     */
    public UpdateClientWithCompatibilityMessage()
    {
        super();
    }

    /**
     * Message creation.
     *
     * @param dummy just pass true to initialize the message for sending.
     */
    public UpdateClientWithCompatibilityMessage(final boolean dummy)
    {
        super();

        this.buffer = new PacketBuffer(Unpooled.buffer());
        IMinecoloniesAPI.getInstance().getColonyManager().getCompatibilityManager().serialize(this.buffer);
    }

    @Override
    public void fromBytes(@NotNull final PacketBuffer buf)
    {
        this.buffer = new PacketBuffer(buf.retain());
    }

    @Override
    public void toBytes(@NotNull final PacketBuffer buf)
    {
        this.buffer.resetReaderIndex();
        buf.writeBytes(this.buffer);
    }

    @Nullable
    @Override
    public Boolean getExecutionSide()
    {
        return Boolean.FALSE;
    }

    @Override
    public void onExecute(final MessageContext ctx, final boolean isLogicalServer)
    {
        final ClientLevel world = Minecraft.getInstance().World;
        try
        {
            IMinecoloniesAPI.getInstance().getColonyManager().getCompatibilityManager().deserialize(this.buffer, world);
        }
        catch (Exception e)
        {
            Log.getLogger().error("Failed to load compatibility manager", e);
        }
        this.buffer.release();
    }
}



