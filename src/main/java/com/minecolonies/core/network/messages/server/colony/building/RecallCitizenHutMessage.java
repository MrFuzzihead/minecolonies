package com.minecolonies.core.network.messages.server.colony.building;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import com.minecolonies.api.util.EntityUtils;
import com.minecolonies.api.util.Log;
import com.minecolonies.api.util.MessageUtils;
import com.minecolonies.core.colony.buildings.views.AbstractBuildingView;
import com.minecolonies.core.network.messages.server.AbstractBuildingServerMessage;
import com.minecolonies.core.util.TeleportHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static com.minecolonies.api.util.constant.TranslationConstants.WARNING_CITIZEN_RECALL_FAILED;

/**
 * Used to handle citizen recalls to their hut.
 */
public class RecallCitizenHutMessage extends AbstractBuildingServerMessage<IBuilding>
{
    /**
     * Empty public constructor.
     */
    public RecallCitizenHutMessage()
    {
        super();
    }

    /**
     * Creates a message to recall all citizens to their hut.
     *
     * @param building {@link AbstractBuildingView}
     */
    public RecallCitizenHutMessage(@NotNull final AbstractBuildingView building)
    {
        super(building);
    }

    @Override
    protected void onExecute(@NotNull final MessageContext ctx, final boolean isLogicalServer, @NotNull final IColony colony, @NotNull final IBuilding building)
    {
        final int[] location = building.getPosition();
        final World world = colony.getWorld();
        for (final ICitizenData citizenData : building.getAllAssignedCitizen())
        {
            Optional<AbstractEntityCitizen> optionalEntityCitizen = citizenData.getEntity();
            if (!optionalEntityCitizen.isPresent())
            {
                Log.getLogger().warn(String.format("Citizen #%d:%d has gone AWOL, respawning them!", colony.getID(), citizenData.getId()));
                citizenData.setNextRespawnPosition(EntityUtils.getSpawnPoint(world, location));
                citizenData.updateEntityIfNecessary();
                optionalEntityCitizen = citizenData.getEntity();
            }

            if (optionalEntityCitizen.isPresent() && !TeleportHelper.teleportCitizen(optionalEntityCitizen.get(), world, location))
            {
                final Player player = ctx.getServerHandler().playerEntity;
                if (player == null)
                {
                    return;
                }

                MessageUtils.format(WARNING_CITIZEN_RECALL_FAILED).sendTo(player);
            }
        }
    }

    @Override
    protected void toBytesOverride(final PacketBuffer buf)
    {

    }

    @Override
    protected void fromBytesOverride(final PacketBuffer buf)
    {

    }
}


