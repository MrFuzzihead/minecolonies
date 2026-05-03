package com.minecolonies.core.util;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.EntityUtils;
import com.minecolonies.api.util.MessageUtils;
// [1.7.10] int[] -> int x,y,z
// [1.7.10] int /* ResourceKey */ -> int dimensionId
import net.minecraft.world.WorldServer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.World.TicketType;
import net.minecraft.world.World.ChunkPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import static com.minecolonies.api.util.constant.translation.CommandTranslationConstants.COMMAND_COLONY_ID_NOT_FOUND;
import static com.minecolonies.api.util.constant.translation.CommandTranslationConstants.COMMAND_TELEPORT_SUCCESS;

/**
 * Helper class for server-side teleporting.
 */
public final class TeleportHelper
{
    private static final double MIDDLE_BLOCK_OFFSET = 0.5D;

    /**
     * Private constructor to hide the implicit public one.
     */
    private TeleportHelper()
    {
        // Intentionally left empty.
    }

    public static boolean teleportCitizen(final AbstractEntityCitizen citizen, final World world, final int[] location)
    {
        if (citizen == null || world == null || world.isClientSide)
        {
            return false;
        }

        final int[] spawnPoint = EntityUtils.getSpawnPoint(world, location);
        if (spawnPoint == null)
        {
            return false;
        }

        if (citizen.getCitizenSleepHandler().isAsleep())
        {
            citizen.getCitizenSleepHandler().onWakeUp();
        }

        citizen.getNavigation().stop();
        citizen.stopRiding();
        citizen.moveTo(
          spawnPoint.getX() + MIDDLE_BLOCK_OFFSET,
          spawnPoint.getY(),
          spawnPoint.getZ() + MIDDLE_BLOCK_OFFSET,
          citizen.getRotationYaw(),
          citizen.getRotationPitch());
        return true;
    }

    /**
     * Teleports the player to his home colony.
     *
     * @param player the player to teleport home.
     */
    public static void homeTeleport(@NotNull final EntityPlayerMP player)
    {
        final IColony colony = IColonyManager.getInstance().getIColonyByOwner(player.getCommandSenderWorld(), player);
        if (colony == null)
        {
            MessageUtils.format(COMMAND_COLONY_ID_NOT_FOUND).sendTo(player);
            return;
        }

        colonyTeleport(player, colony);
    }

    /**
     * Teleports the player to the nearest safe surface location above their current location
     */
    public static void surfaceTeleport(@NotNull final EntityPlayerMP player)
    {
        int[] position = new int[]{(int)player.posX, 250, (int)player.posZ}; //start at current position
        final ServerLevel world = (ServerLevel) player.World;

        position = BlockPosUtil.findLand(position, world);

        ChunkPos chunkpos = new ChunkPos(position);
        world.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkpos, 1, player.getId());
        player.stopRiding();
        if (player.isSleeping())
        {
            player.stopSleepInBed(true, true);
        }

        player.teleportTo(world, position.getX(), position.getY() + 2.0, position.getZ(), player.getYRot(), player.getXRot());
    }

    /**
     * Teleports the player to his home colony.
     *
     * @param dimension the dimension the colony is in.
     * @param player    the player to teleport.
     * @param id        the colony id.
     */
    public static void colonyTeleportByID(@NotNull final EntityPlayerMP player, final int id, final int /* ResourceKey */ dimension)
    {
        final IColony colony = IColonyManager.getInstance().getColonyByDimension(id, dimension);
        if (colony == null)
        {
            MessageUtils.format(COMMAND_COLONY_ID_NOT_FOUND).sendTo(player);
            return;
        }

        colonyTeleport(player, colony);
    }

    /**
     * Teleports the player to the given colony.
     *
     * @param player the player to teleport.
     * @param colony the colony to teleport to.
     */
    public static void colonyTeleport(@NotNull final EntityPlayerMP player, @NotNull final IColony colony)
    {
        colonyTeleport(player, colony, null);
    }

    /**
     * Teleports the player to the given colony.
     *
     * @param player the player to teleport.
     * @param colony the colony to teleport to.
     * @param pos the preferred position to teleport to.
     */
    public static void colonyTeleport(@NotNull final EntityPlayerMP player, @NotNull final IColony colony, final int[] pos)
    {
        int[] position = pos;
        if (pos == null)
        {
            if (colony.getServerBuildingManager().getTownHall() != null)
            {
                position = colony.getServerBuildingManager().getTownHall().getPosition();
            }
            else
            {
                position = colony.getCenter();
            }
        }

        final ServerLevel world = player.getServer().getLevel(colony.getDimension());

        position = BlockPosUtil.findAround(world,
          position,
          5,
          5,
          (predWorld, predPos) -> predWorld.getBlockState(predPos).isAir() && predWorld.getBlockState(predPos.above()).isAir());

        if (position == null)
        {
            return;
        }

        ChunkPos chunkpos = new ChunkPos(position);
        world.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkpos, 1, player.getId());
        player.stopRiding();
        if (player.isSleeping())
        {
            player.stopSleepInBed(true, true);
        }

        player.teleportTo(world, position.getX(), position.getY(), position.getZ(), player.getYRot(), player.getXRot());
        MessageUtils.format(COMMAND_TELEPORT_SUCCESS, colony.getName()).sendTo(player);
    }
}



