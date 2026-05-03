package com.minecolonies.core.network.messages.client;

import com.minecolonies.api.colony.workorders.WorkOrderType;
import com.minecolonies.api.network.IMessage;
import com.minecolonies.core.network.messages.server.DecorationBuildRequestMessage;
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] int[] -> int x,y,z
import net.minecraft.world.Mirror;
import net.minecraft.world.Rotation;

/**
 * Message to open the deco build window on the client.
 */
public class OpenDecoBuildWindowMessage extends OpenBuildWindowMessage
{
    public OpenDecoBuildWindowMessage()
    {
    }

    /**
     * Create a new message.
     *
     * @param pos      the position the deco will be anchored at.
     * @param packName the pack of the deco.
     * @param path     the path in the pack.
     */
    public OpenDecoBuildWindowMessage(
      final int[] pos,
      final String packName,
      final String path,
      final Rotation rotation,
      final Mirror mirror)
    {
        super(pos, packName, path, rotation, mirror);
    }

    @Override
    public IMessage createWorkOrderMessage(final int[] builder)
    {
        return new DecorationBuildRequestMessage(WorkOrderType.BUILD, pos, packName, path, Minecraft.getInstance().World.dimension(), rotation, mirror, builder);
    }
}



