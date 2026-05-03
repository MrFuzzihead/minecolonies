package com.minecolonies.api.util;

import com.minecolonies.api.colony.buildings.IBuilding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

import static com.minecolonies.api.util.constant.CitizenConstants.NIGHT;
import static com.minecolonies.api.util.constant.CitizenConstants.NOON;

/**
 * Class which has world related util functions like chunk load checks
 */
public class WorldUtil
{
    /**
     * Checks if the block is loaded for block access
     *
     * @param world world to use
     * @param x     block x
     * @param z     block z
     * @return true if block is accessible/loaded
     */
    public static boolean isBlockLoaded(final World world, final int x, final int z)
    {
        return isChunkLoaded(world, x >> 4, z >> 4);
    }

    /**
     * Returns whether a chunk is fully loaded
     *
     * @param world world to check on
     * @param chunkX chunk x
     * @param chunkZ chunk z
     * @return true if loaded
     */
    public static boolean isChunkLoaded(final World world, final int chunkX, final int chunkZ)
    {
        return world.getChunkProvider().chunkExists(chunkX, chunkZ);
    }

    /**
     * Checks if the block is loaded for ticking entities
     *
     * @param world world to use
     * @param x     block x
     * @param z     block z
     * @return true if block is accessible/loaded
     */
    public static boolean isEntityBlockLoaded(final World world, final int x, final int z)
    {
        return isChunkLoaded(world, x >> 4, z >> 4);
    }

    /**
     * Mark a chunk at a position dirty if loaded.
     *
     * @param world the world to mark it dirty in.
     * @param x     block x within the chunk.
     * @param y     block y.
     * @param z     block z within the chunk.
     */
    public static void markChunkDirty(final World world, final int x, final int y, final int z)
    {
        if (isBlockLoaded(world, x, z))
        {
            world.getChunkFromBlockCoords(x, z).setChunkModified();
            world.markBlockForUpdate(x, y, z);
        }
    }

    /**
     * Check if it's currently day in the world.
     *
     * @param world the world to check.
     * @return true if so.
     */
    public static boolean isDayTime(final World world)
    {
        return world.getWorldTime() % 24000 <= NIGHT;
    }

    /**
     * Check if it's currently past a certain time in the world.
     *
     * @param world    the world to check.
     * @param pastTime the time to compare.
     * @return true if so.
     */
    public static boolean isPastTime(final World world, final int pastTime)
    {
        return world.getWorldTime() % 24000 <= pastTime;
    }

    /**
     * Check if it's currently afternoon in the world.
     *
     * @param world the world to check.
     * @return true if so.
     */
    public static boolean isPastNoon(final World world)
    {
        return isPastTime(world, NOON);
    }

    /**
     * Check if a world is the overworld.
     *
     * @param world the world to check.
     * @return true if so.
     */
    public static boolean isOverworldType(@NotNull final World world)
    {
        return world.provider.dimensionId == 0;
    }

    /**
     * Check if a world is the nether.
     *
     * @param world the world to check.
     * @return true if so.
     */
    public static boolean isNetherType(@NotNull final World world)
    {
        return world.provider.dimensionId == -1;
    }

    /**
     * Check to see if the world is peaceful.
     *
     * @param world world to check
     * @return true if peaceful
     */
    public static boolean isPeaceful(@NotNull final World world)
    {
        return !world.getGameRules().getGameRuleBooleanValue("doMobSpawning") || world.difficultySetting == EnumDifficulty.PEACEFUL;
    }

    /**
     * Set a block at a position in the world.
     *
     * @param world world to use
     * @param x     block x
     * @param y     block y
     * @param z     block z
     * @param block the block to set
     * @param meta  block metadata
     * @return true if success
     */
    public static boolean setBlockState(final World world, final int x, final int y, final int z, final net.minecraft.block.Block block, final int meta)
    {
        return world.setBlock(x, y, z, block, meta, 3);
    }

    /**
     * Remove a block from the world.
     *
     * @param world    world to remove a block
     * @param x        block x
     * @param y        block y
     * @param z        block z
     * @param isMoving moving flag
     * @return true if success
     */
    public static boolean removeBlock(final World world, final int x, final int y, final int z, final boolean isMoving)
    {
        return world.setBlockToAir(x, y, z);
    }

    /**
     * Get all entities within a building.
     *
     * @param <T>       the type of the predicate.
     * @param world     the world to check this for.
     * @param clazz     the entity class.
     * @param building  the building to check the range for.
     * @param predicate the predicate to check.
     * @return a list of all within those borders.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Entity> List<? extends T> getEntitiesWithinBuilding(
      final @NotNull World world,
      final @NotNull Class<? extends T> clazz,
      final @NotNull IBuilding building,
      @Nullable final Predicate<? super T> predicate)
    {
        final Tuple<int[], int[]> corners = building.getCorners();
        final int[] a = corners.getA();
        final int[] b = corners.getB();

        final List<T> entities = world.getEntitiesWithinAABB(clazz,
          net.minecraft.util.AxisAlignedBB.getBoundingBox(a[0], a[1], a[2], b[0], b[1], b[2]));

        if (predicate == null)
        {
            return entities;
        }

        final List<T> result = new java.util.ArrayList<>();
        for (final T entity : entities)
        {
            if (predicate.test(entity))
            {
                result.add(entity);
            }
        }
        return result;
    }

    /**
     * Returns world max height (always 256 in 1.7.10).
     *
     * @return 256
     */
    public static int getDimensionMaxHeight()
    {
        return 256;
    }

    /**
     * Returns world min height (always 0 in 1.7.10).
     *
     * @return 0
     */
    public static int getDimensionMinHeight()
    {
        return 0;
    }

    /**
     * Check if a given block y is within world bounds.
     *
     * @param yBlock block y
     * @param world  the world
     * @return true if in bounds
     */
    public static boolean isInWorldHeight(final int yBlock, final World world)
    {
        return yBlock >= 0 && yBlock < 256;
    }

    /**
     * Get nearest player, our own cheaper check.
     *
     * @param EntityLivingBase the entity to check.
     * @param x            pos x
     * @param y            pos y
     * @param z            pos z
     * @param lookDistance min distance.
     * @return closest player or null.
     */
    @Nullable
    public static EntityPlayer getNearestPlayer(final EntityCreature EntityLivingBase, final int x, final int y, final int z, final double lookDistance)
    {
        return (EntityPlayer) getNearestEntity(EntityLivingBase.worldObj.playerEntities, EntityLivingBase, x, y, z, lookDistance);
    }

    /**
     * Get the closest entity, cheaper than mojank.
     *
     * @param entityList   entity list to check.
     * @param EntityLivingBase the entity they should be close to.
     * @param x            pos x
     * @param y            pos y
     * @param z            pos z
     * @param lookDistance max distance.
     * @return the entity or null.
     * @param <T> type of entity.
     */
    @Nullable
    public static <T extends EntityLivingBase> T getNearestEntity(
      final List<? extends T> entityList,
      @Nullable final EntityCreature EntityLivingBase,
      final int x,
      final int y,
      final int z,
      final double lookDistance)
    {
        double currentEntityDistance = lookDistance * lookDistance;
        T closestEntity = null;

        for (final T entity : entityList)
        {
            if (entity == EntityLivingBase)
            {
                continue;
            }
            if (entity.isInvisible())
            {
                continue;
            }

            final double dx = entity.posX - x;
            final double dy = entity.posY - y;
            final double dz = entity.posZ - z;
            final double entityDistance = dx * dx + dy * dy + dz * dz;

            if (entityDistance > currentEntityDistance)
            {
                continue;
            }

            if (EntityLivingBase != null && !EntityLivingBase.canEntityBeSeen(entity))
            {
                continue;
            }

            if (entityDistance < currentEntityDistance)
            {
                currentEntityDistance = entityDistance;
                closestEntity = entity;
            }
        }

        return closestEntity;
    }
}

