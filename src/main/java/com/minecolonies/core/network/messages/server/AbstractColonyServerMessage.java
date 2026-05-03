package com.minecolonies.core.network.messages.server;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.permissions.Action;
import com.minecolonies.api.network.IMessage;
import com.minecolonies.api.util.MessageUtils;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import org.jetbrains.annotations.Nullable;

import static com.minecolonies.api.util.constant.TranslationConstants.HUT_BLOCK_MISSING_COLONY;
import static com.minecolonies.api.util.constant.translation.ToolTranslationConstants.TOOL_PERMISSION_SCEPTER_PERMISSION_DENY;

/**
 * Abstract base for all server-side colony messages.
 * [1.7.10] Ported: NetworkEvent.Context → MessageContext; dimensionId is int; Boolean.TRUE → Boolean.TRUE.
 */
public abstract class AbstractColonyServerMessage implements IMessage
{
    /** The dimensionId this message originates from. [1.7.10] plain int */
    private int dimensionId;

    /** The colonyId this message originates from */
    private int colonyId;

    public AbstractColonyServerMessage() {}

    public AbstractColonyServerMessage(final IColony colony)
    {
        this(colony.getDimension(), colony.getID());
    }

    public AbstractColonyServerMessage(final int dimensionId, final int colonyId)
    {
        this.dimensionId = dimensionId;
        this.colonyId = colonyId;
    }

    @Nullable
    public Action permissionNeeded()
    {
        return Action.MANAGE_HUTS;
    }

    public boolean ownerOnly()
    {
        return false;
    }

    protected abstract void onExecute(final MessageContext ctx, final boolean isLogicalServer, final IColony colony);

    protected abstract void toBytesOverride(final PacketBuffer buf);

    protected void toBytesAbstractOverride(final PacketBuffer buf) {}

    @Override
    public final void toBytes(final PacketBuffer buf)
    {
        // [1.7.10] dimensionId is plain int
        buf.writeInt(dimensionId);
        buf.writeInt(colonyId);
        toBytesAbstractOverride(buf);
        toBytesOverride(buf);
    }

    protected abstract void fromBytesOverride(final PacketBuffer buf);

    protected void fromBytesAbstractOverride(final PacketBuffer buf) {}

    @Override
    public final void fromBytes(final PacketBuffer buf)
    {
        // [1.7.10] dimensionId is plain int
        this.dimensionId = buf.readInt();
        this.colonyId = buf.readInt();
        fromBytesAbstractOverride(buf);
        fromBytesOverride(buf);
    }

    @Override
    public final Boolean getExecutionSide()
    {
        return Boolean.TRUE; // server only
    }

    @Override
    public final void onExecute(final MessageContext ctx, final boolean isLogicalServer)
    {
        // [1.7.10] getSender() → ctx.getServerHandler().playerEntity
        final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        final IColony colony = IColonyManager.getInstance().getColonyByDimension(colonyId, dimensionId);
        if (colony != null)
        {
            if (!ownerOnly() && permissionNeeded() != null && !colony.getPermissions().hasPermission(player, permissionNeeded()))
            {
                if (player == null)
                {
                    return;
                }
                MessageUtils.format(TOOL_PERMISSION_SCEPTER_PERMISSION_DENY).sendTo(player);
                return;
            }
            else if (ownerOnly() && (player == null || colony.getPermissions().getOwner().equals(player.getGameProfile().getId())))
            {
                if (player == null)
                {
                    return;
                }
                MessageUtils.format(TOOL_PERMISSION_SCEPTER_PERMISSION_DENY).sendTo(player);
                return;
            }

            onExecute(ctx, isLogicalServer, colony);
        }
        else
        {
            MessageUtils.format(HUT_BLOCK_MISSING_COLONY, this.getClass().getSimpleName()).sendTo(player);
        }
    }
}
