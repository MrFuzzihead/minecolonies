package com.minecolonies.core.network.messages.server;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.network.IMessage;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.MessageUtils;
import com.minecolonies.core.MineColonies;
import com.minecolonies.core.Network;
import com.minecolonies.core.colony.Colony;
import com.minecolonies.core.network.messages.client.OpenCantFoundColonyWarningMessage;
import com.minecolonies.core.network.messages.client.OpenColonyFoundingCovenantMessage;
import com.minecolonies.core.network.messages.client.OpenDeleteAbandonColonyMessage;
import com.minecolonies.core.network.messages.client.OpenReactivateColonyMessage;
import com.minecolonies.core.tileentities.TileEntityColonyBuilding;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.util.IChatComponent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static com.minecolonies.api.util.constant.BuildingConstants.DEACTIVATED;
import static com.minecolonies.api.util.constant.TranslationConstants.HUT_BLOCK_MISSING_BUILDING;
import static com.minecolonies.core.MineColonies.getConfig;

/**
 * Message for asking the server for some colony info before creation.
 */
public class GetColonyInfoMessage implements IMessage
{
    /**
     * Position the player wants to found the colony at.
     */
    int[] pos;

    public GetColonyInfoMessage()
    {
        super();
    }

    public GetColonyInfoMessage(final int[] pos)
    {
        this.pos = pos;
    }

    @Override
    public void toBytes(final PacketBuffer buf)
    {
        buf.writeBlockPos(pos);
    }

    @Override
    public void fromBytes(final PacketBuffer buf)
    {
        pos = buf.readBlockPos();
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
        final EntityPlayerMP sender = ctx.getServerHandler().playerEntity;
        final World world = ctx.getServerHandler().playerEntity.World;

        if (sender == null)
        {
            return;
        }

        if (IColonyManager.getInstance().getColonyByPosFromWorld(world, pos) instanceof Colony)
        {
            MessageUtils.format(HUT_BLOCK_MISSING_BUILDING).sendTo(sender);
            return;
        }

        if (IColonyManager.getInstance().getIColonyByOwner(world, sender) instanceof Colony colony)
        {
            Network.getNetwork().sendToPlayer(new OpenDeleteAbandonColonyMessage(pos, colony.getName(), colony.getCenter(), colony.getID()), sender);
            return;
        }

        final IColony nextColony = IColonyManager.getInstance().getClosestColony(world, pos);
        if (IColonyManager.getInstance().isFarEnoughFromColonies(world, pos))
        {
            final double spawnDistance = Math.sqrt(BlockPosUtil.getDistanceSquared2D(pos, world.getSharedSpawnPos()));
            if (spawnDistance < MineColonies.getConfig().getServer().minDistanceFromWorldSpawn.get())
            {
                Network.getNetwork().sendToPlayer(new OpenCantFoundColonyWarningMessage(String.translatable("com.minecolonies.core.founding.tooclosetospawn", (int) (MineColonies.getConfig().getServer().minDistanceFromWorldSpawn.get() - spawnDistance)), pos, true), sender);
            }
            else if (spawnDistance > MineColonies.getConfig().getServer().maxDistanceFromWorldSpawn.get())
            {
                Network.getNetwork().sendToPlayer(new OpenCantFoundColonyWarningMessage(String.translatable("com.minecolonies.core.founding.toofarfromspawn", (int) (spawnDistance - MineColonies.getConfig().getServer().maxDistanceFromWorldSpawn.get())), pos, true), sender);
            }
            else if (world.getBlockEntity(pos) instanceof TileEntityColonyBuilding townhall && townhall.getPositionedTags().containsKey(new int[]{0,0,0}) && townhall.getPositionedTags().get(new int[]{0,0,0}).contains(DEACTIVATED))
            {
                Network.getNetwork().sendToPlayer(new OpenReactivateColonyMessage(nextColony == null ? "" : nextColony.getName(), nextColony == null ? Integer.MAX_VALUE : (int) BlockPosUtil.getDistance(nextColony.getCenter(), pos) - (getConfig().getServer().initialColonySize.get() << 4), pos), sender);
            }
            else
            {
                Network.getNetwork().sendToPlayer(new OpenColonyFoundingCovenantMessage(nextColony == null ? "" : nextColony.getName(), nextColony == null ? Integer.MAX_VALUE : (int) BlockPosUtil.getDistance(nextColony.getCenter(), pos) - (getConfig().getServer().initialColonySize.get() << 4), pos), sender);
            }
        }
        else
        {
            if (nextColony == null)
            {
                return;
            }

            final int blockRange = Math.max(MineColonies.getConfig().getServer().minColonyDistance.get(), getConfig().getServer().initialColonySize.get()) << 4;
            final int distance = (int) BlockPosUtil.getDistance(pos, nextColony.getCenter());

            Network.getNetwork().sendToPlayer(new OpenCantFoundColonyWarningMessage(String.translatable("com.minecolonies.core.founding.tooclosetocolony", Math.max(100, blockRange - distance)), pos, false), sender);
        }
    }
}




