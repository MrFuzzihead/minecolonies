package com.minecolonies.core.network.messages.server.colony;

import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.core.network.messages.server.AbstractColonyServerMessage;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
// [1.7.10] int /* ResourceKey */ -> int dimensionId
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * Message to trigger a response handler on the server side.
 */
public class InteractionResponse extends AbstractColonyServerMessage
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
     * The chosen response.
     */
    private int responseId;

    /**
     * Empty public constructor.
     */
    public InteractionResponse()
    {
        super();
    }

    /**
     * Trigger the server response handler.
     *
     * @param colonyId   the colony id.
     * @param citizenId  the citizen id.
     * @param dimension  the dimension the colony and citizen are in.
     * @param key        the key of the handler.
     * @param responseId the response to trigger.
     */
    public InteractionResponse(
      final int colonyId,
      final int citizenId,
      final int /* ResourceKey */ dimension,
      @NotNull final String key,
      final int responseId)
    {
        super(dimension, colonyId);
        this.citizenId = citizenId;
        this.key = key;
        this.responseId = responseId;
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
        this.responseId = buf.readInt();
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
        buf.writeInt(responseId);
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
            citizenData.onResponseTriggered(key, responseId, ctx.getServerHandler().playerEntity);
        }
    }
}




