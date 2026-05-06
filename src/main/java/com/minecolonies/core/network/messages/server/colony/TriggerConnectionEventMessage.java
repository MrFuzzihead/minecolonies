package com.minecolonies.core.network.messages.server.colony;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.connections.ConnectionEvent;
import com.minecolonies.api.colony.connections.IColonyConnectionManager;
import com.minecolonies.api.colony.permissions.Action;
import com.minecolonies.api.util.Log;
import com.minecolonies.core.network.messages.server.AbstractColonyServerMessage;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Message for triggering a connection event message at a given colony from another colony.
 */
public class TriggerConnectionEventMessage extends AbstractColonyServerMessage
{
    /**
     * Set the connection event data.
     */
    private ConnectionEvent connectionEventData;

    /**
     * Target colony id.
     */
    private int targetColonyId;

    public TriggerConnectionEventMessage()
    {

    }

    public TriggerConnectionEventMessage(final IColony colony, final ConnectionEvent coreConnectionEventData, final int targetColonyId)
    {
        super(colony);
        this.connectionEventData = coreConnectionEventData;
        this.targetColonyId = targetColonyId;
    }

    @Override
    protected void onExecute(final MessageContext ctx, final boolean isLogicalServer, final IColony colony)
    {
        final Player player = ctx.getServerHandler().playerEntity;
        if (player == null)
        {
            return;
        }

        if (colony.getPermissions().hasPermission(player, Action.MANAGE_HUTS))
        {
            final IColony targetColony = IColonyManager.getInstance().getColonyByDimension(targetColonyId, colony.getDimension());
            if (targetColony == null)
            {
                Log.getLogger().error("Tried to trigger connection event at null colony: {}", targetColonyId);
                return;
            }

            targetColony.getConnectionManager().triggerConnectionEvent(connectionEventData);
        }
    }

    @Override
    protected void toBytesOverride(final PacketBuffer buf)
    {
        connectionEventData.serializeByteBuf(buf);
        buf.writeInt(targetColonyId);
    }

    @Override
    protected void fromBytesOverride(final PacketBuffer buf)
    {
        connectionEventData = ConnectionEvent.deserializeByteBuf(buf);
        targetColonyId = buf.readInt();
    }
}


