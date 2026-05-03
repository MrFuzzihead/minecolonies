package com.minecolonies.core.entity.pathfinding.pathjobs;

import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.core.entity.pathfinding.MNode;
import com.minecolonies.core.entity.pathfinding.PathfindingUtils;
import com.minecolonies.core.entity.pathfinding.SurfaceType;
import com.minecolonies.core.entity.pathfinding.pathresults.PathResult;
// [1.7.10] int[] -> int x,y,z
// [1.7.10] world.entity removed
import net.minecraft.world.World;
import net.minecraft.entity.EntityCreature;
// [1.7.10] block import removed
import org.jetbrains.annotations.NotNull;

/**
 * Job that handles moving close to a position near another
 */
public class PathJobMoveCloseToXNearY extends AbstractPathJob implements IDestinationPathJob
{
    /**
     * Position to go close to
     */
    public final int[] desiredPosition;

    /**
     * Position to stay nearby
     */
    public final int[] nearbyPosition;

    /**
     * Required distance to reach
     */
    public final int distToDesired;

    public PathJobMoveCloseToXNearY(
      final World world,
      final int[] desiredPosition,
      final int[] nearbyPosition,
      final int distToDesired,
      final EntityCreature entity)
    {
        super(world, PathfindingUtils.prepareStart(entity), desiredPosition, new PathResult<PathJobMoveCloseToXNearY>(), entity);

        this.desiredPosition = desiredPosition;
        this.nearbyPosition = nearbyPosition;
        this.distToDesired = distToDesired;
        extraNodes = 20;
    }

    @Override
    protected double computeHeuristic(final int x, final int y, final int z)
    {
        return BlockPosUtil.distManhattan(desiredPosition, x, y, z) + BlockPosUtil.distManhattan(nearbyPosition, x, y, z) * 2;
    }

    @Override
    protected boolean isAtDestination(@NotNull final MNode n)
    {
        if (desiredPosition.getX() == n.x && desiredPosition.getZ() == n.z)
        {
            return false;
        }

        return BlockPosUtil.distManhattan(desiredPosition, n.x, n.y, n.z) <= distToDesired
                 && SurfaceType.getSurfaceType(world, cachedBlockLookup.getBlockState(n.x, n.y - 1, n.z), tempWorldPos.set(n.x, n.y - 1, n.z), getPathingOptions())
                      == SurfaceType.WALKABLE;
    }

    @Override
    protected double getEndNodeScore(@NotNull final MNode n)
    {
        if (desiredPosition.getX() == n.x && desiredPosition.getZ() == n.z)
        {
            return 1000;
        }

        double dist = BlockPosUtil.distManhattan(desiredPosition, n.x, n.y, n.z) * 2 + BlockPosUtil.distManhattan(nearbyPosition, n.x, n.y, n.z);
        if (n.isSwimming())
        {
            dist += 50;
        }
        else if (cachedBlockLookup.getBlockState(n.x, n.y - 1, n.z) == Blocks.WATER.defaultBlockState())
        {
            dist += 50;
        }

        return dist;
    }

    @Override
    protected boolean stopOnNodeLimit(final int totalNodesVisited, final MNode bestNode, final int nodesSinceEndNode)
    {
        if (nodesSinceEndNode > 200)
        {
            return true;
        }
        else
        {
            maxNodes += 200;
            return false;
        }
    }

    @Override
    public int[] getDestination()
    {
        return desiredPosition;
    }

    /**
     * Helper to compare if the given move close to X near Y job matches the input parameters
     *
     * @return true if the given job is the same
     */
    public static boolean isJobFor(final AbstractPathJob job, final int[] desiredPosition, final int[] nearbyPosition, final int distance)
    {
        if (job instanceof PathJobMoveCloseToXNearY pathJob)
        {
            return pathJob.desiredPosition.equals(desiredPosition) && pathJob.nearbyPosition.equals(nearbyPosition) && pathJob.distToDesired == distance;
        }

        return false;
    }
}



