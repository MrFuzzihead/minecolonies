package com.minecolonies.core.colony.crafting;

import com.minecolonies.api.network.IMessage;
// [1.7.10] client removed (use @SideOnly)
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The message used to synchronize crafter recipes from a server to a client.
 */
public class CustomRecipeManagerMessage implements IMessage
{
    /**
     * The buffer with the data.
     */
    private PacketBuffer managerBuffer;

    /**
     * Empty constructor used when registering the message
     */
    public CustomRecipeManagerMessage()
    {
        super();
    }

    /**
     * Add or Update a CustomRecipeManager on the client.
     *
     * @param buf               the bytebuffer.
     */
    public CustomRecipeManagerMessage(final PacketBuffer buf)
    {
        this.managerBuffer = new PacketBuffer(buf.copy());
    }

    @Override
    public void fromBytes(@NotNull final PacketBuffer buf)
    {
        managerBuffer = new PacketBuffer(buf.retain());
    }

    @Override
    public void toBytes(@NotNull final PacketBuffer buf)
    {
        buf.writeBytes(managerBuffer);
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
            CustomRecipeManager.getInstance().handleCustomRecipeManagerMessage(managerBuffer);
        }
        managerBuffer.release();
    }
}


