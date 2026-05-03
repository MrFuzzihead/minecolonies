package com.minecolonies.core.network.messages.server;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.api.util.Log;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.network.PacketBuffer;

/**
 * Abstract base for all server-side building messages.
 * [1.7.10] Ported: NetworkEvent.Context → MessageContext; buildingId as int[] (3 ints).
 */
public abstract class AbstractBuildingServerMessage<T extends IBuilding> extends AbstractColonyServerMessage
{
    /** The buildingID this message originates from. [1.7.10] int[] {x, y, z} */
    private int[] buildingId;

    public AbstractBuildingServerMessage() {}

    public AbstractBuildingServerMessage(IBuildingView building)
    {
        this(building.getColony().getDimension(), building.getColony().getID(), building.getID());
    }

    public AbstractBuildingServerMessage(final int dimensionId, final int colonyId, final int[] buildingId)
    {
        super(dimensionId, colonyId);
        this.buildingId = buildingId;
    }

    public boolean errorIfCastFails()
    {
        return true;
    }

    protected abstract void onExecute(final MessageContext ctx, final boolean isLogicalServer, final IColony colony, final T building);

    @Override
    protected final void toBytesAbstractOverride(final PacketBuffer buf)
    {
        // [1.7.10] write 3 ints instead of BlockPos
        buf.writeInt(buildingId[0]);
        buf.writeInt(buildingId[1]);
        buf.writeInt(buildingId[2]);
    }

    @Override
    protected final void fromBytesAbstractOverride(final PacketBuffer buf)
    {
        // [1.7.10] read 3 ints instead of BlockPos
        this.buildingId = new int[]{buf.readInt(), buf.readInt(), buf.readInt()};
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void onExecute(final MessageContext ctx, final boolean isLogicalServer, final IColony colony)
    {
        final IBuilding building = colony.getServerBuildingManager().getBuilding(buildingId);
        if (building == null)
        {
            return;
        }

        try
        {
            onExecute(ctx, isLogicalServer, colony, (T) building);
        }
        catch (ClassCastException e)
        {
            if (errorIfCastFails())
            {
                Log.getLogger().warn("onExecute called with wrong type: ", e);
            }
        }
    }
}
