package com.minecolonies.core.network.messages.client;

import com.minecolonies.api.colony.workorders.WorkOrderType;
import com.minecolonies.api.network.IMessage;
import com.minecolonies.core.network.messages.server.PlantationFieldBuildRequestMessage;
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] int[] -> int x,y,z
import net.minecraft.world.Mirror;
import net.minecraft.world.Rotation;

/**
 * Message to open the plantation field build window on the client.
 */
public class OpenPlantationFieldBuildWindowMessage extends OpenBuildWindowMessage
{
    public OpenPlantationFieldBuildWindowMessage()
    {
        super();
    }

    /**
     * Create a new message.
     *
     * @param pos      the position the plantation field will be anchored at.
     * @param packName the pack of the plantation field.
     * @param path     the path in the pack.
     */
    public OpenPlantationFieldBuildWindowMessage(
      final int[] pos,
      final String packName,
      final String path,
      final Rotation rotation,
      final Mirror mirror)
    {
        super(pos, packName, path, rotation, mirror);
    }

    @Override
    protected IMessage createWorkOrderMessage(final int[] builder)
    {
        return new PlantationFieldBuildRequestMessage(WorkOrderType.BUILD, pos, packName, path, Minecraft.getInstance().World.dimension(), rotation, mirror, builder);
    }
}



