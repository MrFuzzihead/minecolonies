package com.minecolonies.core.network.messages.server.colony;

import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.core.network.messages.server.AbstractColonyServerMessage;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.util.IChatComponent;
// [1.7.10] int /* ResourceKey */ -> int dimensionId
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * Message to trigger a response handler close on the server side.
 */
public class InteractionClose extends AbstractColonyServerMessage
{
    /**
     * Id of the citizen.
     */
    private int citizenId;

    /**
     * The key of the handler to trigger.
     */
    private String key;

    /**
     * Empty public constructor.
     */
    public InteractionClose()
    {
        super();
    }

    /**
     * Trigger the server response handler.
     *
     * @param colonyId  the colony id.
     * @param citizenId the citizen id.
     * @param dimension the dimension the colony and citizen are in.
     * @param key       the key of the handler.
     */
    public InteractionClose(
      final int colonyId,
      final int citizenId,
      final int /* ResourceKey */ dimension,
      @NotNull final String key)
    {
        super(dimension, colonyId);
        this.citizenId = citizenId;
        this.key = key;
    }

    /**
     * Transformation from a byteStream to the variables.
     *
     * @param buf the used byteBuffer.
     */
    @Override
    public void fromBytesOverride(@NotNull final PacketBuffer buf)
    {
        this.citizenId = buf.readInt();
        this.key = buf.readComponent();
    }

    /**
     * Transformation to a byteStream.
     *
     * @param buf the used byteBuffer.
     */
    @Override
    public void toBytesOverride(@NotNull final PacketBuffer buf)
    {
        buf.writeInt(this.citizenId);
        buf.writeComponent(key);
    }

    @Override
    protected void onExecute(final MessageContext ctx, final boolean isLogicalServer, final IColony colony)
    {
        ICitizenData citizenData = colony.getCitizenManager().getCivilian(citizenId);
        if (citizenData == null)
        {
            citizenData = colony.getVisitorManager().getVisitor(citizenId);
        }

        if (citizenData != null && ctx.getServerHandler().playerEntity != null)
        {
            citizenData.onInteractionClosed(key, ctx.getServerHandler().playerEntity);
        }
    }
}




