package com.minecolonies.core.entity.pathfinding.pathjobs;

import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.core.entity.pathfinding.MNode;
import com.minecolonies.core.entity.pathfinding.PathfindingUtils;
import com.minecolonies.core.entity.pathfinding.PathingOptions;
import com.minecolonies.core.entity.pathfinding.SurfaceType;
import com.minecolonies.core.entity.pathfinding.pathresults.PathResult;
// [1.7.10] int[] -> int x,y,z
// [1.7.10] world.entity removed
import net.minecraft.world.World;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.AxisAlignedBB;
// [1.7.10] world.phys removed
import org.jetbrains.annotations.NotNull;

/**
 * Job that handles random pathing.
 */
public class PathJobRandomPos extends AbstractPathJob implements IDestinationPathJob
{
    /**
     * Direction to walk to.
     */
    @NotNull
    protected final int[] destination;

    /**
     * Required avoidDistance.
     */
    protected final int minDistFromStart;

    /**
     * Minimum distance to the goal.
     */
    private final int maxDistToDest;

    /**
     * If inside paths should be preferred.
     */
    private boolean preferInside = false;

    /**
     * Box restriction area
     */
    private AxisAlignedBB  restrictionBox = null;
    private int[] restrictionBoxCenter = null;

    /**
     * Mutable int[] to use where necessary.
     */
    private final int[] mutablePos = new int[]{0,0,0};

    /**
     * Prepares the PathJob for the path finding system.
     *
     * @param world            world the entity is in.
     * @param start            starting location.
     * @param minDistFromStart how far to move away.
     * @param range            max range to search.
     * @param entity           the entity.
     */
    public PathJobRandomPos(
      final World world,
      @NotNull final int[] start,
      final int minDistFromStart,
      final int range,
      final EntityCreature entity)
    {
        super(world, start, range, new PathResult<PathJobRandomPos>(), entity);
        this.minDistFromStart = minDistFromStart;
        this.maxDistToDest = -1;

        this.destination = BlockPosUtil.getRandomPosAround(start, minDistFromStart);
    }

    /**
     * Prepares the PathJob for the path finding system.
     *
     * @param world            world the entity is in.
     * @param start            starting location.
     * @param minDistFromStart how far to move away.
     * @param searchRange      max range to search.
     * @param entity           the entity.
     */
    public PathJobRandomPos(
      final World world,
      @NotNull final int[] start,
      final int minDistFromStart,
      final int searchRange,
      final int maxDistToDest,
      final EntityCreature entity,
      @NotNull final int[] dest)
    {
        super(world, start, searchRange, new PathResult<PathJobRandomPos>(), entity);
        this.minDistFromStart = minDistFromStart;
        this.maxDistToDest = maxDistToDest;
        this.destination = dest;
    }

    /**
     * Prepares the PathJob for the path finding system.
     *
     * @param world            world the entity is in.
     * @param start            starting location.
     * @param minDistFromStart how far to move away.
     * @param range            max range to search.
     * @param entity           the entity.
     */
    public PathJobRandomPos(
      final World world,
      @NotNull final int[] start,
      final int minDistFromStart,
      final int range,
      final EntityCreature entity,
      final int[] startRestriction,
      final int[] endRestriction)
    {
        super(world, start, range, new PathResult<PathJobRandomPos>(), entity);

        restrictionBox = AxisAlignedBB.getBoundingBox(Math.min(startRestriction[0], endRestriction[0]),
          Math.min(startRestriction[1], endRestriction[1]),
          Math.min(startRestriction[2], endRestriction[2]),
          Math.max(startRestriction[0], endRestriction[0]),
          Math.max(startRestriction[1], endRestriction[1]),
          Math.max(startRestriction[2], endRestriction[2]));
        restrictionBoxCenter = new int[]{(int)((restrictionBox.minX + restrictionBox.maxX) / 2), (int)((restrictionBox.minY + restrictionBox.maxY) / 2), (int)((restrictionBox.minZ + restrictionBox.maxZ) / 2)};
        this.minDistFromStart = minDistFromStart;
        this.maxDistToDest = -1;

        this.destination = BlockPosUtil.getRandomPosAround(start, minDistFromStart);
    }

    /**
     * Prepares the PathJob for the path finding system.
     *
     * @param world            world the entity is in.
     * @param start            starting location.
     * @param minDistFromStart how far to move away.
     * @param range            max range to search.
     * @param entity           the entity.
     * @param preferInside        if the entity should try to stay inside.
     */
    public PathJobRandomPos(
        final World world,
        @NotNull final int[] start,
        final int minDistFromStart,
        final int range,
        final EntityCreature entity,
        final int[] startRestriction,
        final int[] endRestriction,
        final boolean preferInside)
    {
        super(world, start, range, new PathResult<PathJobRandomPos>(), entity);

        restrictionBox = AxisAlignedBB.getBoundingBox(Math.min(startRestriction[0], endRestriction[0]),
            Math.min(startRestriction[1], endRestriction[1]),
            Math.min(startRestriction[2], endRestriction[2]),
            Math.max(startRestriction[0], endRestriction[0]),
            Math.max(startRestriction[1], endRestriction[1]),
            Math.max(startRestriction[2], endRestriction[2]));
        restrictionBoxCenter = new int[]{(int)((restrictionBox.minX + restrictionBox.maxX) / 2), (int)((restrictionBox.minY + restrictionBox.maxY) / 2), (int)((restrictionBox.minZ + restrictionBox.maxZ) / 2)};
        this.minDistFromStart = minDistFromStart;
        this.maxDistToDest = -1;
        this.preferInside = preferInside;
        this.destination = BlockPosUtil.getRandomPosAround(start, minDistFromStart);
        maxNodes = restrictionBox == null ? 2000 : 1000;
    }

    /**
     * Check if there is space a few blocks above without solid blocks.
     * @param x x pos.
     * @param y y pos.
     * @param z z pos.
     * @return true if so.
     */
    private boolean hasSpaceAbove(final int x, final int y, final int z)
    {
        for (int i = 1; i < 5; i++)
        {
            if (cachedBlockLookup.getBlockState(x,y+i+1,z).isSolid())
            {
                return true;
            }
        }
        return false;
    }

    @Override
    protected double computeHeuristic(final int x, final int y, final int z)
    {
        if (restrictionBox != null)
        {
            return (BlockPosUtil.distManhattan(destination, x, y, z) + BlockPosUtil.distManhattan(restrictionBoxCenter, x, y, z) / 2.0);
        }

        return BlockPosUtil.distManhattan(destination, x, y, z);
    }

    @Override
    protected boolean isAtDestination(@NotNull final MNode n)
    {
        if ((restrictionBox == null || (n.x >= restrictionBox.minX && n.x <= restrictionBox.maxX && n.y >= restrictionBox.minY && n.y <= restrictionBox.maxY && n.z >= restrictionBox.minZ && n.z <= restrictionBox.maxZ))
              && BlockPosUtil.distSqr(start, n.x, n.y, n.z) > minDistFromStart * minDistFromStart
              && (maxDistToDest == -1 || BlockPosUtil.distSqr(destination, n.x, n.y, n.z) < this.maxDistToDest * this.maxDistToDest)
              && (getPathingOptions().canWalkUnderWater() || !PathfindingUtils.isWater(cachedBlockLookup, tempWorldPos.set(n.x, n.y - 1, n.z)))
              && SurfaceType.getSurfaceType(cachedBlockLookup, cachedBlockLookup.getBlockState(n.x, n.y - 1, n.z), tempWorldPos.set(n.x, n.y - 1, n.z), getPathingOptions())
                   == SurfaceType.WALKABLE)
        {
            if (preferInside && hasSpaceAbove(n.x,n.y,n.z))
            {
                return false;
            }

            return true;
        }
        return false;
    }

    @Override
    protected double getEndNodeScore(@NotNull final MNode n)
    {
        return BlockPosUtil.distManhattan(start, n.x, n.y, n.z);
    }

    @Override
    public void setPathingOptions(final PathingOptions pathingOptions)
    {
        super.setPathingOptions(pathingOptions);
        getPathingOptions().canDrop = false;
    }

    @Override
    public int[] getDestination()
    {
        return destination;
    }

    /**
     * Helper to compare if the given random pos job matches the input parameters
     *
     * @return true if the given job is the same
     */
    public static boolean isJobFor(final AbstractPathJob job, final int[] center, final int range)
    {
        if (job instanceof PathJobRandomPos pathJob)
        {
            return pathJob.destination != null && pathJob.destination.equals(center) && pathJob.maxDistToDest == range;
        }

        return false;
    }
}



