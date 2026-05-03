package com.minecolonies.core.network.messages.server.colony;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.core.network.messages.server.AbstractColonyServerMessage;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

/**
 * Message to set the colony default structure style.
 */
public class ColonyStructureStyleMessage extends AbstractColonyServerMessage
{
    /**
     * The chosen pack.
     */
    private String pack;

    /**
     * Default constructor
     **/
    public ColonyStructureStyleMessage()
    {
        super();
    }

    /**
     * Change the colony default pack from the client to the serverside.
     *
     * @param colony the colony the player changed the pack in.
     * @param pack   the pack name,
     */
    public ColonyStructureStyleMessage(final IColony colony, final String pack)
    {
        super(colony);
        this.pack = pack;
    }

    @Override
    protected void onExecute(MessageContext ctx, boolean isLogicalServer, IColony colony)
    {
        colony.setStructurePack(pack);
    }

    @Override
    protected void toBytesOverride(PacketBuffer buf)
    {
        buf.writeUtf(pack);
    }

    @Override
    protected void fromBytesOverride(PacketBuffer buf)
    {
        this.pack = buf.readUtf(32767);
    }
}



