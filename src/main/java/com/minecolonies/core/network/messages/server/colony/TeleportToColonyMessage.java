package com.minecolonies.core.network.messages.server.colony;
import net.minecraft.tileentity.BlockEntity; // [1.7.10] alias -> TileEntity

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.connections.DiplomacyStatus;
import com.minecolonies.api.colony.permissions.Action;
import com.minecolonies.api.util.InventoryUtils;
import com.minecolonies.api.util.MathUtils;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingGateHouse;
import com.minecolonies.core.network.messages.server.AbstractColonyServerMessage;
import com.minecolonies.core.tileentities.TileEntityColonyBuilding;
import com.minecolonies.core.util.TeleportHelper;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
// [1.7.10] int /* ResourceKey */ -> int dimensionId
import net.minecraft.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.World;
// [1.7.10] block.entity removed
// [1.7.10] items shim in com.minecolonies.api.shim
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.minecolonies.api.util.constant.Constants.STACKSIZE;
import static com.minecolonies.api.util.constant.SchematicTagConstants.TAG_GATE;

/**
 * Message for trying to teleport to a friends colony.
 */
public class TeleportToColonyMessage extends AbstractColonyServerMessage
{
    /**
     * Origin colony id.
     */
    private int originColonyId;

    /**
     * Teleportation cost.
     */
    private int cost;

    /**
     * Gatehouse pos to teleport to.
     */
    private int[] pos;

    public TeleportToColonyMessage()
    {
        super();
    }

    public TeleportToColonyMessage(final int /* ResourceKey */ dimensionId, final int colonyId, final int[] pos, final int originColonyId, final int cost)
    {
        super(dimensionId, colonyId);
        this.pos = pos;
        this.originColonyId = originColonyId;
        this.cost = cost;
    }

    @Nullable
    @Override
    public Action permissionNeeded()
    {
        return null;
    }

    @Override
    protected void onExecute(final MessageContext ctx, final boolean isLogicalServer, final IColony colony)
    {
        if (ctx.getServerHandler().playerEntity == null)
        {
            return;
        }

        final IColony originColony = IColonyManager.getInstance().getColonyByDimension(originColonyId, ctx.getServerHandler().playerEntity.World.dimension());
        if (originColony == null)
        {
            return;
        }

        if (originColony.getConnectionManager().getColonyDiplomacyStatus(colony.getID()) != DiplomacyStatus.ALLIES)
        {
            return;
        }

        if (originColony.getPermissions().hasPermission(ctx.getServerHandler().playerEntity, Action.TELEPORT_TO_COLONY) || colony.getPermissions().hasPermission(ctx.getServerHandler().playerEntity, Action.TELEPORT_TO_COLONY))
        {
            final BlockEntity gateHouse = colony.getWorld().getBlockEntity(pos);
            if (gateHouse instanceof TileEntityColonyBuilding && ((TileEntityColonyBuilding) gateHouse).getBuilding() instanceof BuildingGateHouse)
            {
                if (cost > 0)
                {
                    if (InventoryUtils.attemptReduceStackInItemHandler(new InvWrapper(ctx.getServerHandler().playerEntity.getInventory()), new ItemStack(Items.GOLD_NUGGET), cost))
                    {
                        int output = cost/2;
                        if (output <= STACKSIZE)
                        {
                            InventoryUtils.addItemStackToItemHandler(((TileEntityColonyBuilding) gateHouse).getInventory(), new ItemStack(Items.GOLD_NUGGET, output));
                        }
                        else
                        {
                            for (int i = 0; i < output/STACKSIZE; i++)
                            {
                                if (output > 0)
                                {
                                    final int qty = Math.min(STACKSIZE, output);
                                    InventoryUtils.addItemStackToItemHandler(((TileEntityColonyBuilding) gateHouse).getInventory(), new ItemStack(Items.GOLD_NUGGET, qty));
                                    output -= qty;
                                }
                            }
                        }
                    }
                }

                final List<int[]> posList = ((TileEntityColonyBuilding) gateHouse).getCachedWorldTagNamePosMap().get(TAG_GATE);
                if (posList == null || posList.isEmpty())
                {
                    TeleportHelper.colonyTeleport(ctx.getServerHandler().playerEntity, colony, pos);
                }
                else
                {
                    TeleportHelper.colonyTeleport(ctx.getServerHandler().playerEntity, colony, posList.get(MathUtils.RANDOM.nextInt(posList.size())));
                }
            }
            else
            {
                TeleportHelper.colonyTeleport(ctx.getServerHandler().playerEntity, colony, pos);
            }
        }
    }

    @Override
    protected void toBytesOverride(final PacketBuffer buf)
    {
        buf.writeBlockPos(pos);
        buf.writeInt(originColonyId);
        buf.writeInt(cost);
    }

    @Override
    protected void fromBytesOverride(final PacketBuffer buf)
    {
        this.pos = buf.readBlockPos();
        this.originColonyId = buf.readInt();
        this.cost = buf.readInt();
    }
}




