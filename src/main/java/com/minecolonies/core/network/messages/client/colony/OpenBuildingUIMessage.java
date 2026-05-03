package com.minecolonies.core.network.messages.client.colony;

import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.network.IMessage;
import com.minecolonies.core.colony.ColonyView;
import com.minecolonies.core.colony.buildings.views.AbstractBuildingView;
// [1.7.10] int[] -> int x,y,z
// [1.7.10] Registries removed
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
// [1.7.10] int /* ResourceKey */ -> int dimensionId
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Add or Update a AbstractBuilding.View to a ColonyView on the client.
 */
public class OpenBuildingUIMessage implements IMessage
{
    private int          colonyId;
    private int[]     buildingId;

    /**
     * Dimension of the colony.
     */
    private int /* ResourceKey */ dimension;

    /**
     * Empty constructor used when registering the
     */
    public OpenBuildingUIMessage()
    {
        super();
    }

    /**
     * Creates a message to handle colony views.
     *
     * @param building AbstractBuilding to add or update a view.
     */
    public OpenBuildingUIMessage(@NotNull final IBuilding building)
    {
        super();
        this.colonyId = building.getColony().getID();
        this.buildingId = building.getID();
        this.dimension = building.getColony().getDimension();
    }

    @Override
    public void fromBytes(@NotNull final PacketBuffer buf)
    {
        colonyId = buf.readInt();
        buildingId = buf.readBlockPos();
        dimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(buf.readUtf(32767)));
    }

    @Override
    public void toBytes(@NotNull final PacketBuffer buf)
    {
        buf.writeInt(colonyId);
        buf.writeBlockPos(buildingId);
        buf.writeUtf(dimension.location().toString());
    }

    @Nullable
    @Override
    public Boolean getExecutionSide()
    {
        return Boolean.FALSE;
    }

    @Override
    public void onExecute(final MessageContext ctx, final boolean isLogicalServer)
    {
        if (IColonyManager.getInstance().getColonyView(colonyId, dimension) instanceof ColonyView colonyView && colonyView.getClientBuildingManager().getBuilding(buildingId) instanceof AbstractBuildingView buildingView)
        {
            buildingView.openGui(false);
        }
    }
}



