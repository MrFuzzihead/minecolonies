package com.minecolonies.core.network.messages.server;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.inventory.container.ContainerCrafting;
import com.minecolonies.api.network.IMessage;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayer;
import org.jetbrains.annotations.Nullable;

/**
 * Creates a message to switch recipe outputs when multiple are available.
 */
public class SwitchRecipeCraftingTeachingMessage implements IMessage
{
    /**
     * Create message.
     */
    public SwitchRecipeCraftingTeachingMessage()
    {
        super();
    }

    @Override
    public void fromBytes(final PacketBuffer buf)
    {
    }

    @Override
    public void toBytes(final PacketBuffer buf)
    {
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
        final Player player = ctx.getServerHandler().playerEntity;
        if (player.containerMenu instanceof ContainerCrafting)
        {
            final ContainerCrafting container = (ContainerCrafting) player.containerMenu;
            container.switchRecipes();
        }
    }
}


