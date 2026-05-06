package com.minecolonies.core.network.messages.server;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.advancements.AdvancementTriggers;
import com.minecolonies.api.network.IMessage;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.Nullable;

public class ClickGuiButtonTriggerMessage implements IMessage
{
    /**
     * The ID of the button clicked;
     */
    private String buttonId;

    /**
     * The window's Resource
     */
    private ResourceLocation resource;

    /**
     * Empty constructor used when registering the message.
     */
    public ClickGuiButtonTriggerMessage()
    {
        super();
    }

    public ClickGuiButtonTriggerMessage(final String buttonId, final ResourceLocation resource)
    {
        super();
        this.resource = resource;
        this.buttonId = buttonId;
    }

    @Override
    public void toBytes(final PacketBuffer buf)
    {
        buf.writeResourceLocation(this.resource);
        buf.writeUtf(this.buttonId);
    }

    @Override
    public void fromBytes(final PacketBuffer buf)
    {
        this.resource = buf.readResourceLocation();
        this.buttonId = buf.readUtf(32767);
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
            AdvancementTriggers.CLICK_GUI_BUTTON.trigger(player, this.buttonId, this.resource);
        }
    }
}



