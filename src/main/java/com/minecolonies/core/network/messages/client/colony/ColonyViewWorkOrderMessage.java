package com.minecolonies.core.network.messages.client.colony;

import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.workorders.IServerWorkOrder;
import com.minecolonies.api.network.IMessage;
import com.minecolonies.core.colony.Colony;
import com.minecolonies.core.colony.workorders.view.AbstractWorkOrderView;
import io.netty.buffer.Unpooled;
// [1.7.10] Registries removed
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
// [1.7.10] int /* ResourceKey */ -> int dimensionId
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Add or Update a ColonyView on the client.
 */
public class ColonyViewWorkOrderMessage implements IMessage
{
    private int                colonyId;
    private int /* ResourceKey */ dimension;
    private PacketBuffer       workOrderBuffer;

    /**
     * Empty constructor used when registering the
     */
    public ColonyViewWorkOrderMessage()
    {
        super();
    }

    /**
     * Updates a {@link AbstractWorkOrderView} of the workOrders.
     *
     * @param colony        colony of the workOrder.
     * @param workOrderList list of workorders to send to the client
     */
    public ColonyViewWorkOrderMessage(@NotNull final Colony colony, @NotNull final List<IServerWorkOrder> workOrderList)
    {
        this.colonyId = colony.getID();
        this.workOrderBuffer = new PacketBuffer(Unpooled.buffer());
        this.dimension = colony.getDimension();

        workOrderBuffer.writeInt(workOrderList.size());
        for (final IServerWorkOrder workOrder : workOrderList)
        {
            workOrder.serializeViewNetworkData(workOrderBuffer);
        }
    }

    @Override
    public void fromBytes(@NotNull final PacketBuffer buf)
    {
        final PacketBuffer newbuf = new PacketBuffer(buf.retain());
        colonyId = newbuf.readInt();
        dimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(newbuf.readUtf(32767)));
        workOrderBuffer = newbuf;
    }

    @Override
    public void toBytes(@NotNull final PacketBuffer buf)
    {
        workOrderBuffer.resetReaderIndex();
        buf.writeInt(colonyId);
        buf.writeUtf(dimension.location().toString());
        buf.writeBytes(workOrderBuffer);
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
        IColonyManager.getInstance().handleColonyViewWorkOrderMessage(colonyId, workOrderBuffer, dimension);
        workOrderBuffer.release();
    }
}





