package com.minecolonies.core.debug.messages;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.network.IMessage;
import com.minecolonies.core.debug.DebugPlayerManager;
// [1.7.10] client removed (use @SideOnly)
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Enables debug mode for the client
 */
public class DebugEnableMessage implements IMessage
{
    /**
     * Whether to enable or disable debug mode
     */
    private boolean enable = true;

    public DebugEnableMessage()
    {
        super();
    }

    public DebugEnableMessage(final boolean enable)
    {
        this.enable = enable;
    }

    @Override
    public void fromBytes(@NotNull final PacketBuffer buf)
    {
        enable = buf.readBoolean();
    }

    @Override
    public void toBytes(@NotNull final PacketBuffer buf)
    {
        buf.writeBoolean(enable);
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
        DebugPlayerManager.setDebugModeFor(Minecraft.getInstance().player.getUUID(), enable);
    }
}



