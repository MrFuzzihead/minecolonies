package com.minecolonies.core.network.messages.server.colony;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.network.IMessage;
import com.minecolonies.api.util.MessageUtils;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayerMP;
import org.jetbrains.annotations.Nullable;

import static com.minecolonies.api.util.constant.TranslationConstants.*;

/**
 * Message for deleting an owned colony
 */
public class ColonyAbandonOwnMessage implements IMessage
{
    @Override
    public void toBytes(final PacketBuffer buf)
    {

    }

    @Override
    public void fromBytes(final PacketBuffer buf)
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
        final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        if (player == null)
        {
            return;
        }

        final IColony colony = IColonyManager.getInstance().getIColonyByOwner(player.World, player);
        if (colony != null)
        {
            colony.getPermissions().setOwnerAbandoned();
            colony.getPermissions().addPlayer(player.getGameProfile(), colony.getPermissions().getRankOfficer());
            MessageUtils.format(MESSAGE_INFO_COLONY_ABANDON_SUCCESS).sendTo(player);
        }
        else
        {
            MessageUtils.format(MESSAGE_INFO_COLONY_NOT_FOUND).sendTo(player);
        }
    }
}



