package com.minecolonies.core.network.messages.server.colony.building;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.core.network.messages.server.AbstractBuildingServerMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
// [1.7.10] int[] -> int x,y,z
import org.jetbrains.annotations.NotNull;

/**
 * Adds a entry to the builderRequired map. Created: May 26, 2014
 *
 * @author Colton
 */
public class BuildRequestMessage extends AbstractBuildingServerMessage<IBuilding>
{
    /**
     * The request mode.
     */
    public enum Mode
    {
        BUILD,
        REPAIR,
        REMOVE
    }

    /**
     * The mode id.
     */
    private Mode mode;

    /**
     * The id of the building.
     */
    private int[] builder;

    /**
     * Empty constructor used when registering the
     */
    public BuildRequestMessage()
    {
        super();
    }

    /**
     * Creates a build request
     *
     * @param building the building we're executing on.
     * @param mode     Mode of the request, 1 is repair, 0 is build.
     * @param builder  the builder we're assinging the request to
     */
    public BuildRequestMessage(@NotNull final IBuildingView building, final Mode mode, final int[] builder)
    {
        super(building);
        this.mode = mode;
        this.builder = builder;
    }

    @Override
    public void fromBytesOverride(@NotNull final PacketBuffer buf)
    {
        mode = Mode.values()[buf.readInt()];
        builder = buf.readBlockPos();
    }

    @Override
    public void toBytesOverride(@NotNull final PacketBuffer buf)
    {
        buf.writeInt(mode.ordinal());
        buf.writeBlockPos(builder);
    }

    @Override
    public void onExecute(final MessageContext ctx, final boolean isLogicalServer, final IColony colony, final IBuilding building)
    {
        final Player player = ctx.getServerHandler().playerEntity;
        if (building.isPendingConstruction())
        {
            building.removeWorkOrder();
        }
        else
        {
            switch (mode)
            {
                case BUILD:
                    building.requestUpgrade(player, builder);
                    break;
                case REPAIR:
                    building.requestRepair(builder);
                    break;
                case REMOVE:
                    building.requestRemoval(player, builder);
                    for (final int[] childPos : building.getChildren())
                    {
                        final IBuilding childBuilding = colony.getServerBuildingManager().getBuilding(childPos);
                        if (childBuilding != null)
                        {
                            childBuilding.requestRemoval(player, builder);
                        }
                    }
                default:
                    break;
            }
        }
    }
}


