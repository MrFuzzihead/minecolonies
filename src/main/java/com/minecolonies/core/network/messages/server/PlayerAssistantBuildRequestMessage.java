package com.minecolonies.core.network.messages.server;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.permissions.Action;
import com.minecolonies.api.colony.workorders.IWorkOrder;
import com.minecolonies.core.items.ItemAssistantHammer;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.util.IChatComponent;
import net.minecraft.entity.player.EntityPlayer;
import org.jetbrains.annotations.Nullable;

/**
 * Adds a entry to the builderRequired map.
 */
public class PlayerAssistantBuildRequestMessage extends AbstractColonyServerMessage
{
    private int      workorderID;
    private int[] interactPos;

    /**
     * Empty constructor used when registering the
     */
    public PlayerAssistantBuildRequestMessage()
    {
        super();
    }

    public PlayerAssistantBuildRequestMessage(final IColony colony, final int workorderID, final int[] interactPos)
    {
        super(colony);
        this.workorderID = workorderID;
        this.interactPos = interactPos;
    }

    @Override
    protected void toBytesOverride(final PacketBuffer buf)
    {
        buf.writeInt(workorderID);
        buf.writeBlockPos(interactPos);
    }

    @Override
    protected void fromBytesOverride(final PacketBuffer buf)
    {
        workorderID = buf.readInt();
        interactPos = buf.readBlockPos();
    }

    @Nullable
    public Action permissionNeeded()
    {
        return Action.PLACE_BLOCKS;
    }

    @Override
    protected void onExecute(final MessageContext ctx, final boolean isLogicalServer, final IColony colony)
    {
        final Player player = ctx.getServerHandler().playerEntity;

        final IWorkOrder workOrder = colony.getWorkManager().getWorkOrder(workorderID);
        if (workOrder == null)
        {
            player.sendSystemMessage(String.literal("Could not find workorder with id: " + workorderID));
            return;
        }

        if (player.getMainHandItem().getItem() instanceof ItemAssistantHammer hammer)
        {
            hammer.placeBlock(player, colony, workOrder, interactPos);
        }
    }
}



