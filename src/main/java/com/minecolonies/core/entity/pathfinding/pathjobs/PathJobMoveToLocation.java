package com.minecolonies.core.entity.pathfinding.pathjobs;

import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.ShapeUtil;
import com.minecolonies.api.util.constant.ColonyConstants;
import com.minecolonies.core.entity.pathfinding.MNode;
import com.minecolonies.core.entity.pathfinding.PathfindingUtils;
import com.minecolonies.core.entity.pathfinding.SurfaceType;
import com.minecolonies.core.entity.pathfinding.pathresults.PathResult;
// [1.7.10] int[] -> int x,y,z
// [1.7.10] world.entity removed
import net.minecraft.world.World;
import net.minecraft.entity.EntityCreature;
import net.minecraft.pathfinding.Path;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Job that handles moving to a location.
 */
public class PathJobMoveToLocation extends AbstractPathJob implements IDestinationPathJob
{
    private static final float    DESTINATION_SLACK_NONE     = 0.1F;
    // 1^2 + 1^2 + 1^2 + (epsilon of 0.1F)
    private static final float    DESTINATION_SLACK_ADJACENT = (float) Math.sqrt(2f);
    @NotNull
    protected final int[] destination;
    // 0 = exact match
    private              float    destinationSlack           = DESTINATION_SLACK_NONE;

    /**
     * Prepares the PathJob for the path finding system.
     *
     * @param world  world the entity is in.
     * @param start  starting location.
     * @param end    target location.
     * @param range  max search range.
     * @param entity the entity.
     */
    public PathJobMoveToLocation(final World world, @NotNull final int[] start, @NotNull final int[] end, final int range, final EntityCreature entity)
    {
        super(world, start, end, new PathResult<PathJobMoveToLocation>(), entity);

        maxNodes += range;
        this.destination = new int[]{end[0], end[1], end[2]};

        extraNodes = 4;
    }

    /**
     * Perform the search.
     *
     * @return Path of a path to the given location, a best-effort, or null.
     */
    @Nullable
    @Override
    protected net.minecraft.pathfinding.PathEntity search()
    {
        //  Compute destination slack - if the destination point cannot be stood in
        if (getGroundHeight(null, destination[0], destination[1], destination[2]) != destination[1])
        {
            destinationSlack = DESTINATION_SLACK_ADJACENT;
        }

        return super.search();
    }

    @Override
    protected double computeHeuristic(final int x, final int y, final int z)
    {
        return BlockPosUtil.distManhattan(destination, x, y, z);
    }

    /**
     * Checks if the target has been reached.
     *
     * @param n Node to test.
     * @return true if has been reached.
     */
    @Override
    protected boolean isAtDestination(@NotNull final MNode n)
    {
        boolean atDest = false;
        if (destinationSlack <= DESTINATION_SLACK_NONE)
        {
            atDest = n.x == destination[0]
                       && n.y == destination[1]
                       && n.z == destination[2];
        }
        else if (n.y == destination[1] - 1)
        {
            atDest = BlockPosUtil.distSqr(destination, n.x, destination[1], n.z) < DESTINATION_SLACK_ADJACENT * DESTINATION_SLACK_ADJACENT;
        }
        else
        {
            atDest = BlockPosUtil.distSqr(destination, n.x, n.y, n.z) < DESTINATION_SLACK_ADJACENT * DESTINATION_SLACK_ADJACENT;
        }

        if (atDest)
        {
            atDest = SurfaceType.getSurfaceType(world, cachedBlockLookup.getBlockState(n.x, n.y - 1, n.z), setPos(n.x, n.y - 1, n.z), getPathingOptions())
                       == SurfaceType.WALKABLE;
        }

        return atDest;
    }

    /**
     * Calculate the distance to the target.
     *
     * @param n Node to test.
     * @return double of the distance.
     */
    @Override
    protected double getEndNodeScore(@NotNull final MNode n)
    {
        if (PathfindingUtils.isLiquid(cachedBlockLookup.getBlockState(n.x, n.y - 1, n.z)))
        {
            return BlockPosUtil.distManhattan(destination, n.x, n.y, n.z) + 30;
        }

        if (!ShapeUtil.isEmpty(cachedBlockLookup.getBlockState(n.x, n.y, n.z).getCollisionShape(cachedBlockLookup, setPos(n.x, n.y, n.z))))
        {
            return BlockPosUtil.distManhattan(destination, n.x, n.y, n.z) + 10;
        }

        //  For Result Score lower is better
        int xDist = Math.abs(destination[0] - n.x);
        int yDist = Math.abs(destination[1] - n.y);
        int zDist = Math.abs(destination[2] - n.z);
        return xDist + yDist + zDist;
    }

    @Override
    protected boolean stopOnNodeLimit(final int totalNodesVisited, final MNode bestNode, final int nodesSinceEndNode)
    {
        // Small chance to go full limit to maybe find a path still, when we did not find any good nodes to move towards
        if (totalNodesVisited < MAX_NODES && BlockPosUtil.distManhattan(start, bestNode.x, bestNode.y, bestNode.z) < 10 && ColonyConstants.rand.nextInt(100) <= 20)
        {
            maxNodes += 1000;
            return false;
        }
        // 10k limit for progressing
        else if (nodesSinceEndNode < 200 && totalNodesVisited < MAX_NODES * 2)
        {
            maxNodes += 500;
            return false;
        }

        return true;
    }

    @Override
    public String toString()
    {
        return super.toString() + " destination:" + destination;
    }

    @Override
    public int[] getDestination()
    {
        return destination;
    }

    /**
     * Helper to compare if the given move to location job matches the input parameters
     *
     * @return true if the given job is the same
     */
    public static boolean isJobFor(final AbstractPathJob job, final int[] desiredPosition)
    {
        if (job instanceof PathJobMoveToLocation pathJob)
        {
            return pathJob.getDestination().equals(desiredPosition);
        }

        return false;
    }
}







