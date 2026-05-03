package com.minecolonies.core.network.messages.server.colony.building;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.core.colony.buildings.modules.WorkerBuildingModule;
import com.minecolonies.core.colony.buildings.workerbuildings.Stash;
import com.minecolonies.core.network.messages.server.AbstractBuildingServerMessage;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.NotNull;

public class ChangeDeliveryPriorityMessage extends AbstractBuildingServerMessage<IBuilding>
{
    /**
     * If up true, if down false.
     */
    private boolean up;

    /**
     * Empty public constructor.
     */
    public ChangeDeliveryPriorityMessage()
    {
        super();
    }

    /**
     * Creates message for player to change the priority of the delivery.
     *
     * @param building view of the building to read data from
     * @param up       up or down?
     */
    public ChangeDeliveryPriorityMessage(@NotNull final IBuildingView building, final boolean up)
    {
        super(building);
        this.up = up;
    }

    /**
     * Transformation from a byteStream to the variables.
     *
     * @param buf the used byteBuffer.
     */
    @Override
    public void fromBytesOverride(@NotNull final PacketBuffer buf)
    {
        this.up = buf.readBoolean();
    }

    /**
     * Transformation to a byteStream.
     *
     * @param buf the used byteBuffer.
     */
    @Override
    public void toBytesOverride(@NotNull final PacketBuffer buf)
    {
        buf.writeBoolean(this.up);
    }

    @Override
    protected void onExecute(final MessageContext ctx, final boolean isLogicalServer, final IColony colony, final IBuilding building)
    {
        if ((building != null && building.hasModule(WorkerBuildingModule.class)) || building instanceof Stash)
        {
            if (up)
            {
                building.alterPickUpPriority(1);
            }
            else
            {
                building.alterPickUpPriority(-1);
            }
            building.markDirty();
        }
    }
}


