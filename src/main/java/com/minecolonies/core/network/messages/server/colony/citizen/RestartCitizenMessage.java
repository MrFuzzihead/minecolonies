package com.minecolonies.core.network.messages.server.colony.citizen;

import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.util.MessageUtils;
import com.minecolonies.core.colony.buildings.views.AbstractBuildingView;
import com.minecolonies.core.network.messages.server.AbstractColonyServerMessage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.NotNull;

import static com.minecolonies.api.util.constant.TranslationConstants.MESSAGE_CITIZEN_RESTART_SCHEDULED;

/**
 * Message class which manages the messages hiring or firing of citizens.
 */
public class RestartCitizenMessage extends AbstractColonyServerMessage
{
    /**
     * The citizen to restart.
     */
    private int citizenID;

    /**
     * Empty public constructor.
     */
    public RestartCitizenMessage()
    {
        super();
    }

    /**
     * Creates object for the player to restart a citizen (instead of the fire/hire solution).
     *
     * @param building  view of the building to read data from
     * @param citizenID the id of the citizen to fill the job.
     */
    public RestartCitizenMessage(@NotNull final AbstractBuildingView building, final int citizenID)
    {
        super(building.getColony());
        this.citizenID = citizenID;
    }

    /**
     * Transformation from a byteStream to the variables.
     *
     * @param buf the used byteBuffer.
     */
    @Override
    public void fromBytesOverride(@NotNull final PacketBuffer buf)
    {

        citizenID = buf.readInt();
    }

    /**
     * Transformation to a byteStream.
     *
     * @param buf the used byteBuffer.
     */
    @Override
    public void toBytesOverride(@NotNull final PacketBuffer buf)
    {

        buf.writeInt(citizenID);
    }

    @Override
    protected void onExecute(final MessageContext ctx, final boolean isLogicalServer, final IColony colony)
    {
        final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        if (player == null)
        {
            return;
        }

        final ICitizenData citizen = colony.getCitizenManager().getCivilian(citizenID);

        // Restart also worker building and AI
        citizen.scheduleRestart(player);
        MessageUtils.format(MESSAGE_CITIZEN_RESTART_SCHEDULED, citizen.getName()).sendTo(player);
    }
}



