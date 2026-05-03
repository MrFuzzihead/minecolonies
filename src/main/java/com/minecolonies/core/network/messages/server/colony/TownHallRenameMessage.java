package com.minecolonies.core.network.messages.server.colony;

import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyView;
import com.minecolonies.api.eventbus.events.colony.ColonyNameChangedModEvent;
import com.minecolonies.core.network.messages.server.AbstractColonyServerMessage;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.NotNull;

/**
 * Message to execute the renaming of the townHall.
 */
public class TownHallRenameMessage extends AbstractColonyServerMessage
{
    private static final int    MAX_NAME_LENGTH  = 25;
    private static final int    SUBSTRING_LENGTH = MAX_NAME_LENGTH - 1;
    private              String name;

    /**
     * Empty public constructor.
     */
    public TownHallRenameMessage()
    {
        super();
    }

    /**
     * Object creation for the town hall rename
     *
     * @param colony Colony the rename is going to occur in.
     * @param name   New name of the town hall.
     */
    public TownHallRenameMessage(@NotNull final IColonyView colony, final String name)
    {
        super(colony);
        this.name = (name.length() <= MAX_NAME_LENGTH) ? name : name.substring(0, SUBSTRING_LENGTH);
    }

    @Override
    public void fromBytesOverride(@NotNull final PacketBuffer buf)
    {
        name = buf.readUtf(32767);
    }

    @Override
    public void toBytesOverride(@NotNull final PacketBuffer buf)
    {
        buf.writeUtf(name);
    }

    @Override
    protected void onExecute(final MessageContext ctx, final boolean isLogicalServer, final IColony colony)
    {
        name = (name.length() <= MAX_NAME_LENGTH) ? name : name.substring(0, SUBSTRING_LENGTH);
        colony.setName(name);
        IMinecoloniesAPI.getInstance().getEventBus().post(new ColonyNameChangedModEvent(colony));
    }
}


