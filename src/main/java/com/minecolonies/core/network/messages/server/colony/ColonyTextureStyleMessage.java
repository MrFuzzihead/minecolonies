package com.minecolonies.core.network.messages.server.colony;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.core.network.messages.server.AbstractColonyServerMessage;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

/**
 * Message to set the colony texture style.
 */
public class ColonyTextureStyleMessage extends AbstractColonyServerMessage
{
    /**
     * The chosen style.
     */
    private String style;

    /**
     * Default constructor
     **/
    public ColonyTextureStyleMessage()
    {
        super();
    }

    /**
     * Change the colony style from the client to the serverside.
     *
     * @param colony      the colony the player changed the style in.
     * @param style the list of patterns they set in the banner picker
     */
    public ColonyTextureStyleMessage(final IColony colony, final String style)
    {
        super(colony);
        this.style = style;
    }

    @Override
    protected void onExecute(MessageContext ctx, boolean isLogicalServer, IColony colony)
    {
        colony.setTextureStyle(style);
    }

    @Override
    protected void toBytesOverride(PacketBuffer buf)
    {
        buf.writeUtf(style);
    }

    @Override
    protected void fromBytesOverride(PacketBuffer buf)
    {
        this.style = buf.readUtf(32767);
    }
}


