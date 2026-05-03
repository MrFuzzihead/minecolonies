package com.minecolonies.core.network.messages.client.colony;

import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.network.IMessage;
import io.netty.buffer.Unpooled;
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
public class ColonyViewBuildingViewMessage implements IMessage
{
    private int          colonyId;
    private int[]     buildingId;
    private PacketBuffer buildingData;

    /**
     * Dimension of the colony.
     */
    private int /* ResourceKey */ dimension;

    /**
     * Empty constructor used when registering the
     */
    public ColonyViewBuildingViewMessage()
    {
        super();
    }

    /**
     * Creates a message to handle colony views.
     *
     * @param building AbstractBuilding to add or update a view.
     */
    public ColonyViewBuildingViewMessage(@NotNull final IBuilding building)
    {
        this(building, true);
    }

    /**
     * Creates a message to handle colony views.
     *
     * @param building AbstractBuilding to add or update a view.
     */
    public ColonyViewBuildingViewMessage(@NotNull final IBuilding building, final boolean fullSync)
    {
        super();
        this.colonyId = building.getColony().getID();
        this.buildingId = building.getID();
        this.buildingData = new PacketBuffer(Unpooled.buffer());
        building.serializeToView(this.buildingData, fullSync);
        this.dimension = building.getColony().getDimension();
    }

    @Override
    public void fromBytes(@NotNull final PacketBuffer buf)
    {
        colonyId = buf.readInt();
        buildingId = buf.readBlockPos();
        dimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(buf.readUtf(32767)));
        buildingData = new PacketBuffer(Unpooled.buffer(buf.readableBytes()));
        buf.readBytes(buildingData, buf.readableBytes());
    }

    @Override
    public void toBytes(@NotNull final PacketBuffer buf)
    {
        buildingData.resetReaderIndex();
        buf.writeInt(colonyId);
        buf.writeBlockPos(buildingId);
        buf.writeUtf(dimension.location().toString());
        buf.writeBytes(buildingData);
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
        IColonyManager.getInstance().handleColonyBuildingViewMessage(colonyId, buildingId, buildingData, dimension);
    }
}



