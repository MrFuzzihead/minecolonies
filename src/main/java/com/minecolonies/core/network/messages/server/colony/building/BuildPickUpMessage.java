package com.minecolonies.core.network.messages.server.colony.building;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.core.network.messages.server.AbstractBuildingServerMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.NotNull;

/**
 * Picks up the building block with the World.
 */
public class BuildPickUpMessage extends AbstractBuildingServerMessage<IBuilding>
{
    /**
     * Empty constructor used when registering the
     */
    public BuildPickUpMessage()
    {
        super();
    }

    /**
     * Creates a build request
     *
     * @param building the building we're executing on.
     */
    public BuildPickUpMessage(@NotNull final IBuildingView building)
    {
        super(building);
    }

    @Override
    public void fromBytesOverride(@NotNull final PacketBuffer buf)
    {

    }

    @Override
    public void toBytesOverride(@NotNull final PacketBuffer buf)
    {

    }

    @Override
    public void onExecute(final MessageContext ctx, final boolean isLogicalServer, final IColony colony, final IBuilding building)
    {
        final Player player = ctx.getServerHandler().playerEntity;
        building.pickUp(player);
    }
}


