package com.minecolonies.core.network.messages.client;

import com.minecolonies.api.network.IMessage;
import com.minecolonies.core.client.render.worldevent.PathfindingDebugRenderer;
import com.minecolonies.core.entity.pathfinding.MNode;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * Message to sync the reached positions over to the client for rendering.
 */
public class SyncPathReachedMessage implements IMessage
{
    /**
     * Set of reached positions.
     */
    public Set<int[]> reached = new HashSet<>();

    /**
     * Default constructor.
     */
    public SyncPathReachedMessage()
    {
        super();
    }

    /**
     * Create the message to send a set of positions over to the client side.
     *
     */
    public SyncPathReachedMessage(final Set<int[]> reached)
    {
        super();
        this.reached = reached;
    }

    @Override
    public void toBytes(final PacketBuffer buf)
    {
        buf.writeInt(reached.size());
        for (final int[] node : reached)
        {
            buf.writeBlockPos(node);
        }
    }

    @Override
    public void fromBytes(final PacketBuffer buf)
    {
        int size = buf.readInt();
        for (int i = 0; i < size; i++)
        {
            reached.add(buf.readBlockPos());
        }
    }

    @Nullable
    @Override
    public Boolean getExecutionSide()
    {
        return Boolean.FALSE;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onExecute(final MessageContext ctx, final boolean isLogicalServer)
    {
        for (final MNode node : PathfindingDebugRenderer.lastDebugNodesPath)
        {
            for (final int[] reachedPos : reached)
            {
                if (reachedPos.getX() == node.x && reachedPos.getY() == node.y && reachedPos.getZ() == node.z)
                {
                    node.setReachedByWorker(true);
                }
            }
        }
    }
}


