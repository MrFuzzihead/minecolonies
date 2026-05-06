package com.minecolonies.core.network.messages.server.colony;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.api.eventbus.events.colony.ColonyTeamColorChangedModEvent;
import com.minecolonies.core.network.messages.server.AbstractColonyServerMessage;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.util.EnumChatFormatting;
import org.jetbrains.annotations.NotNull;

/**
 * Message class which manages changing the team color of the colony.
 */
public class TeamColonyColorChangeMessage extends AbstractColonyServerMessage
{
    /**
     * The color to set.
     */
    private int colorOrdinal;

    /**
     * Empty public constructor.
     */
    public TeamColonyColorChangeMessage()
    {
        super();
    }

    /**
     * Creates object for the player to handle the color
     *
     * @param colorOrdinal the color to set.
     * @param building     view of the building to read data from
     */
    public TeamColonyColorChangeMessage(final int colorOrdinal, @NotNull final IBuildingView building)
    {
        super(building.getColony());
        this.colorOrdinal = colorOrdinal;
    }

    /**
     * Transformation from a byteStream to the variables.
     *
     * @param buf the used byteBuffer.
     */
    @Override
    public void fromBytesOverride(@NotNull final PacketBuffer buf)
    {

        colorOrdinal = buf.readInt();
    }

    /**
     * Transformation to a byteStream.
     *
     * @param buf the used byteBuffer.
     */
    @Override
    public void toBytesOverride(@NotNull final PacketBuffer buf)
    {
        buf.writeInt(colorOrdinal);
    }

    @Override
    protected void onExecute(final MessageContext ctx, final boolean isLogicalServer, final IColony colony)
    {
        colony.setColonyColor(ChatFormatting.values()[colorOrdinal]);
        IMinecoloniesAPI.getInstance().getEventBus().post(new ColonyTeamColorChangedModEvent(colony));
    }
}


