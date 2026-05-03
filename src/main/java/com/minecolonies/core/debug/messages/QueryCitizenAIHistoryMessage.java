package com.minecolonies.core.debug.messages;

import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.ICitizenDataView;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.core.Network;
import com.minecolonies.core.debug.DebugPlayerManager;
import com.minecolonies.core.entity.citizen.EntityCitizen;
import com.minecolonies.core.network.messages.server.AbstractColonyServerMessage;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.util.IChatComponent;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
import net.minecraft.entity.player.EntityPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Message to query ai history from the server
 */
public class QueryCitizenAIHistoryMessage extends AbstractColonyServerMessage
{
    /**
     * Citizen id
     */
    private int id;

    public QueryCitizenAIHistoryMessage()
    {
        super();
    }

    public QueryCitizenAIHistoryMessage(final ICitizenDataView citizen)
    {
        super(citizen.getColony());
        this.id = citizen.getId();
    }

    @Override
    public void fromBytesOverride(@NotNull final PacketBuffer buf)
    {
        this.id = buf.readInt();
    }

    @Override
    public void toBytesOverride(@NotNull final PacketBuffer buf)
    {
        buf.writeInt(id);
    }

    @Override
    public void onExecute(final MessageContext ctx, final boolean isLogicalServer, final IColony colony)
    {
        final Player player = ctx.getServerHandler().playerEntity;
        if (player == null || !DebugPlayerManager.hasDebugEnabled(player))
        {
            return;
        }

        final ICitizenData citizen = colony.getCitizenManager().getCivilian(id);
        if (citizen == null || !citizen.getEntity().isPresent())
        {
            return;
        }

        if (citizen.getEntity().get() instanceof EntityCitizen entityCitizen)
        {
            String message = String.literal("Citizen AI: ").append(entityCitizen.getCitizenAI().getHistory());

            if (entityCitizen.getCitizenJobHandler().getColonyJob() != null)
            {
                message.append(String.literal("Job AI: ").append(entityCitizen.getCitizenJobHandler().getWorkAI().getStateAI().getHistory()));
            }

            Network.getNetwork().sendToPlayer(new DebugOutputMessage(message, true), ctx.getServerHandler().playerEntity);
        }
    }
}



