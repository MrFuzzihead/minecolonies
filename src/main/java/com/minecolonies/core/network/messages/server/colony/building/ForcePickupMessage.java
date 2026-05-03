package com.minecolonies.core.network.messages.server.colony.building;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.api.util.MessageUtils;
import com.minecolonies.core.network.messages.server.AbstractBuildingServerMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.NotNull;

import static com.minecolonies.api.colony.requestsystem.requestable.deliveryman.AbstractDeliverymanRequestable.getPlayerActionPriority;
import static com.minecolonies.api.util.constant.TranslationConstants.COM_MINECOLONIES_COREMOD_ENTITY_DELIVERYMAN_FORCEPICKUP;
import static com.minecolonies.api.util.constant.TranslationConstants.COM_MINECOLONIES_COREMOD_ENTITY_DELIVERYMAN_FORCEPICKUP_FAILED;

/**
 * Message class which manages the messages to request an immediate pickup
 */
public class ForcePickupMessage extends AbstractBuildingServerMessage<IBuilding>
{
    /**
     * Empty public constructor.
     */
    public ForcePickupMessage()
    {
        super();
    }

    /**
     * Creates message for player to force a pickup.
     *
     * @param building view of the building to read data from
     */
    public ForcePickupMessage(@NotNull final IBuildingView building)
    {
        super(building);
    }

    @Override
    public void fromBytesOverride(@NotNull final PacketBuffer buf)
    {
        // Noop
    }

    @Override
    public void toBytesOverride(@NotNull final PacketBuffer buf)
    {
        // Noop
    }

    @Override
    protected void onExecute(final MessageContext ctx, final boolean isLogicalServer, final IColony colony, final IBuilding building)
    {
        if (building.createPickupRequest(getPlayerActionPriority(true)))
        {
            MessageUtils.format(COM_MINECOLONIES_COREMOD_ENTITY_DELIVERYMAN_FORCEPICKUP).sendTo(ctx.getServerHandler().playerEntity);
            building.markDirty();
        }
        else
        {
            final Player player = ctx.getServerHandler().playerEntity;
            if (player == null)
            {
                return;
            }

            MessageUtils.format(COM_MINECOLONIES_COREMOD_ENTITY_DELIVERYMAN_FORCEPICKUP_FAILED).sendTo(player);
        }
    }
}


