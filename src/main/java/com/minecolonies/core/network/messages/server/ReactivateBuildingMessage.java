package com.minecolonies.core.network.messages.server;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.permissions.Action;
import com.minecolonies.api.network.IMessage;
import com.minecolonies.core.tileentities.TileEntityColonyBuilding;
import com.minecolonies.core.colony.buildings.AbstractBuilding;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
// [1.7.10] block.entity removed
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Reactivate a building.
 */
public class ReactivateBuildingMessage implements IMessage
{
    /**
     * The position to reactivate it.
     */
    private int[] pos;

    /**
     * Empty constructor used when registering the
     */
    public ReactivateBuildingMessage()
    {
        super();
    }

    /**
     * Reactivate the building.
     *
     * @param pos the position of the building.
     */
    public ReactivateBuildingMessage(final int[] pos)
    {
        super();
        this.pos = pos;
    }

    /**
     * Reads this packet from a {@link PacketBuffer}.
     *
     * @param buf The buffer begin read from.
     */
    @Override
    public void fromBytes(@NotNull final PacketBuffer buf)
    {
        pos = buf.readBlockPos();
    }

    /**
     * Writes this packet to a {@link PacketBuffer}.
     *
     * @param buf The buffer being written to.
     */
    @Override
    public void toBytes(@NotNull final PacketBuffer buf)
    {
        buf.writeBlockPos(pos);
    }

    @Nullable
    @Override
    public Boolean getExecutionSide()
    {
        return Boolean.TRUE;
    }

    @Override
    public void onExecute(final MessageContext ctx, final boolean isLogicalServer)
    {
        final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        final World world = player.getCommandSenderWorld();
        final IColony colony = IColonyManager.getInstance().getColonyByPosFromWorld(world, pos);
        if (colony != null && colony.getPermissions().hasPermission(player, Action.MANAGE_HUTS))
        {
            AbstractBuilding building = (AbstractBuilding) colony.getServerBuildingManager().getBuilding(pos);
            if (building == null)
            {
                final BlockEntity tileEntity = world.getBlockEntity(pos);
                if (tileEntity instanceof final TileEntityColonyBuilding hut)
                {
                    if (!colony.getServerBuildingManager().canPlaceAt(tileEntity.getBlockState().getBlock(), pos, player))
                    {
                        return;
                    }

                    hut.reactivate();
                    colony.getServerBuildingManager().addNewBuilding(hut, world);
                }
            }
        }
    }
}




