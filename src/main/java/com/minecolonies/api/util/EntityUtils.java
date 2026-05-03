package com.minecolonies.api.util;

import com.ldtteam.structurize.util.BlockUtils;
import com.minecolonies.api.entity.other.AbstractFastMinecoloniesEntity;
import com.minecolonies.api.items.ModTags;
import com.minecolonies.core.entity.pathfinding.PathfindingUtils;
import com.minecolonies.core.entity.pathfinding.SurfaceType;
import com.minecolonies.core.entity.pathfinding.navigation.EntityNavigationUtils;
// [1.7.10] int[] -> int x,y,z
// [1.7.10] Direction -> net.minecraft.util.EnumFacing
// [1.7.10] tags removed
import net.minecraft.util.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
// [1.7.10] world.entity removed
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
// [1.7.10] BlockState -> int metadata
import net.minecraftforge.common.util.FakePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

// [1.7.10] HORIZONTAL_DIRS removed - not defined in BlockPosUtil yet

/**
 * Entity related utilities.
 */
public final class EntityUtils
{
    private static final int    AIR_SPACE_ABOVE_TO_CHECK = 2;
    private static final int    DEFAULT_MOVE_RANGE        = 3;
    private static final int    TELEPORT_RANGE            = 512;
    private static final double MIDDLE_BLOCK_OFFSET       = 0.5D;
    private static final int    SCAN_RADIUS               = 5;

    private EntityUtils() {}

    /**
     * Checks if a player is a fakePlayer and tries to get the owning player if possible.
     */
    @NotNull
    public static EntityPlayer getPlayerOfFakePlayer(@NotNull final EntityPlayer player, @NotNull final World world)
    {
        if (player instanceof FakePlayer)
        {
            final EntityPlayer tempPlayer = world.getPlayerEntityByUUID(player.getUniqueID());
            if (tempPlayer != null)
            {
                return tempPlayer;
            }
        }
        return player;
    }

    /**
     * Returns the loaded Entity with the given UUID.
     */
    public static Entity getPlayerByUUID(@NotNull final World world, @NotNull final UUID id)
    {
        return world.getPlayerEntityByUUID(id);
    }

    /**
     * Returns a list of loaded entities whose id's match the ones provided.
     */
    public static List<Entity> getEntitiesFromID(@NotNull final World world, @NotNull final List<Integer> ids)
    {
        return ids.stream()
                 .map(id -> world.getEntityByID(id))
                 .collect(Collectors.toList());
    }

    /**
     * Returns the new rotation degree calculated from the current and intended rotation up to a max.
     */
    public static double updateRotation(final double currentRotation, final double intendedRotation, final double maxIncrement)
    {
        double wrappedAngle = MathHelper.wrapAngleTo180_double(intendedRotation - currentRotation);

        if (wrappedAngle > maxIncrement) wrappedAngle = maxIncrement;
        if (wrappedAngle < -maxIncrement) wrappedAngle = -maxIncrement;

        return currentRotation + wrappedAngle;
    }

    /**
     * Check for free space AIR_SPACE_ABOVE_TO_CHECK blocks high and solid ground.
     */
    public static boolean checkForFreeSpace(@NotNull final World world, @NotNull final int[] groundPosition)
    {
        for (int i = 1; i < AIR_SPACE_ABOVE_TO_CHECK; i++)
        {
            final int ny = groundPosition[1] + i;
            if (solidOrLiquid(world, new int[]{groundPosition[0], ny, groundPosition[2]}))
            {
                return false;
            }
        }
        return BlockUtils.isAnySolid(world, groundPosition[0], groundPosition[1], groundPosition[2]);
    }

    /**
     * Checks if a block position in a world is solid or liquid.
     */
    public static boolean solidOrLiquid(@NotNull final World world, @NotNull final int[] blockPos)
    {
        return world.getBlock(blockPos[0], blockPos[1], blockPos[2]).getMaterial().isLiquid()
                 || BlockUtils.isAnySolid(world, blockPos[0], blockPos[1], blockPos[2]);
    }

    /**
     * Get a safe spawnpoint near a location.
     */
    @Nullable
    public static int[] getSpawnPoint(final World world, final int[] nearPoint)
    {
        return BlockPosUtil.findAround(world, nearPoint, SCAN_RADIUS, SCAN_RADIUS,
          (w, p) -> checkValidSpawn(w, p, 2));
    }

    private static boolean checkValidSpawn(@NotNull final World world, final int[] pos, final int height)
    {
        for (int dy = 0; dy < height; dy++)
        {
            // [1.7.10] No BlockTag.LEAVES equivalent; skip leaves check
            if (solidOrLiquid(world, new int[]{pos[0], pos[1] + dy, pos[2]}))
            {
                return false;
            }
        }
        return SurfaceType.getSurfaceType(world, world.getBlock(pos[0], pos[1] - 1, pos[2]), new int[]{pos[0], pos[1] - 1, pos[2]}) == SurfaceType.WALKABLE
                 || SurfaceType.getSurfaceType(world, world.getBlock(pos[0], pos[1] - 2, pos[2]), new int[]{pos[0], pos[1] - 2, pos[2]}) == SurfaceType.WALKABLE;
    }

    /**
     * Sets the movement of the entity to specific point.
     */
    public static boolean tryMoveLivingToXYZ(@NotNull final EntityCreature living, final int x, final int y, final int z)
    {
        return tryMoveLivingToXYZ(living, x, y, z, 1.0D);
    }

    /**
     * Sets the movement of the entity to specific point with speed factor.
     */
    public static boolean tryMoveLivingToXYZ(@NotNull final EntityCreature living, final int x, final int y, final int z, final double speedFactor)
    {
        if (living instanceof AbstractFastMinecoloniesEntity entity)
        {
            return EntityNavigationUtils.walkToPos(entity, new int[]{x, y, z}, 4, true, speedFactor);
        }
        return true;
    }

    /**
     * Checks if a entity is at his working site. If not, teleports it.
     */
    public static boolean isLivingAtSiteWithMove(@NotNull final EntityLivingBase entity, final int x, final int y, final int z, final int range)
    {
        if (x == 0 && y == 0 && z == 0) return false;

        if (!isLivingAtSite(entity, x, y, z, TELEPORT_RANGE))
        {
            int[] spawnPoint = getSpawnPoint(entity.worldObj, new int[]{x, y, z});
            if (spawnPoint == null) spawnPoint = new int[]{x, y, z};

            entity.setPosition(
              spawnPoint[0] + MIDDLE_BLOCK_OFFSET,
              spawnPoint[1],
              spawnPoint[2] + MIDDLE_BLOCK_OFFSET);
            return true;
        }

        return EntityUtils.isLivingAtSite(entity, x, y, z, range);
    }

    /**
     * Returns whether or not the entity is within a specific range of his working site.
     */
    public static boolean isLivingAtSite(@NotNull final EntityLivingBase entityLiving, final int x, final int y, final int z, final int range)
    {
        final int[] pos = new int[]{(int) entityLiving.posX, (int) entityLiving.posY, (int) entityLiving.posZ};
        return BlockPosUtil.distSqr(pos, x, y, z) < MathUtils.square(range);
    }

    /**
     * Checks if the target is flying.
     */
    public static boolean isFlying(final EntityLivingBase target)
    {
        return target != null && !target.onGround && target.fallDistance <= 0.1f
                 && target.worldObj.isAirBlock(
                   (int) target.posX,
                   (int) target.posY - 2,
                   (int) target.posZ);
    }

    /**
     * Entity pushable by predicate.
     * [1.7.10] Simplified — no EntitySelector.NO_SPECTATORS equivalent.
     */
    public static Predicate<Entity> pushableBy()
    {
        return entity -> entity != null && entity.canBePushed();
    }
}

