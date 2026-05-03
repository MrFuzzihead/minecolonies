package com.minecolonies.core.network.messages.server;

import com.minecolonies.api.advancements.AdvancementTriggers;
import com.minecolonies.api.network.IMessage;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.Nullable;

public class OpenGuiWindowTriggerMessage implements IMessage
{
    /**
     * The window's Resource
     */
    private ResourceLocation resource;

    /**
     * Empty constructor used when registering the message.
     */
    public OpenGuiWindowTriggerMessage()
    {
        super();
    }

    public OpenGuiWindowTriggerMessage(final ResourceLocation resource)
    {
        super();
        this.resource = resource;
    }

    @Override
    public void toBytes(final PacketBuffer buf)
    {
        buf.writeResourceLocation(this.resource);
    }

    @Override
    public void fromBytes(final PacketBuffer buf)
    {
        this.resource = buf.readResourceLocation();
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
        if (player != null)
        {
            AdvancementTriggers.OPEN_GUI_WINDOW.trigger(player, this.resource);
        }
    }
}



