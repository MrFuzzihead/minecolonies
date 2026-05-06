package com.minecolonies.core.entity.pathfinding.pathjobs;
import net.minecraft.util.Direction;
import net.minecraft.block.state.BlockState;

import com.ldtteam.structurize.util.BlockUtils;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.Log;
import com.minecolonies.api.util.Pond;
import com.minecolonies.api.util.Pond.PondState;
import com.minecolonies.api.util.Tuple;
import com.minecolonies.core.entity.pathfinding.MNode;
import com.minecolonies.core.entity.pathfinding.PathfindingUtils;
import com.minecolonies.core.entity.pathfinding.PathingOptions;
import com.minecolonies.core.entity.pathfinding.SurfaceType;
import com.minecolonies.core.entity.pathfinding.pathresults.PathResult;
import com.minecolonies.core.entity.pathfinding.pathresults.WaterPathResult;
// [1.7.10] int[] -> int x,y,z
// [1.7.10] MutableBlockPos removed
// [1.7.10] world.entity removed
import net.minecraft.world.World;
// [1.7.10] LevelReader -> IBlockAccess
import net.minecraft.world.IBlockAccess;
// [1.7.10] BlockState -> int metadata
import net.minecraft.pathfinding.Path;
import net.minecraft.entity.EntityCreature;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Find and return a path to the nearest water. Created: March 25, 2016
 */
public class PathJobFindWater extends AbstractPathJob implements ISearchPathJob
{
    private static final int MAX_RANGE = 100;
    private final        int[]                        hutLocation;
    @NotNull
    private final        List<Tuple<int[], int[]>> ponds;

    /**
     * AbstractPathJob constructor.
     *
     * @param world  the world within which to path.
     * @param start  the start position from which to path from.
     * @param home   the position of the worker hut.
     * @param range  maximum path range.
     * @param ponds  already visited fishing places.
     * @param entity the entity.
     */
    public PathJobFindWater(
        final World world,
        @NotNull final int[] start,
        final int[] home,
        final int range,
        @NotNull final List<Tuple<int[], int[]>> ponds,
        final EntityCreature entity)
    {
        super(world, start, range, new WaterPathResult(), entity);
        this.ponds = new ArrayList<>(ponds);
        hutLocation = home;
    }

    @NotNull
    @Override
    public WaterPathResult getResult()
    {
        return (WaterPathResult) super.getResult();
    }

    @Override
    protected double computeHeuristic(final int x, final int y, final int z)
    {
        return BlockPosUtil.distManhattan(hutLocation, x, y, z);
    }

    @Override
    protected boolean isAtDestination(@NotNull final MNode n)
    {
        if (BlockPosUtil.distSqr(hutLocation, n.x, n.y, n.z) > MAX_RANGE * MAX_RANGE)
        {
            return false;
        }

        final MutableBlockPos problemPos = debugDrawEnabled ? new int[]{0,0,0}.mutable() : null;
        PondState pondState = Pond.checkPond(world, tempWorldPos.set(n.x, n.y - 1, n.z), problemPos);

        if (n.isSwimming() && pondState != PondState.INVALID)
        {
            for (Tuple<int[], int[]> existingPond : ponds)
            {
                if (BlockPosUtil.distManhattan(existingPond.getA(), n.x, n.y, n.z) < Pond.WATER_POOL_WIDTH_REQUIREMENT + 2)
                {
                    return false;
                }
            }

            final PathJobFindFishingPos job = new PathJobFindFishingPos(getActualWorld(), world, new int[]{n.x, n.y, n.z}, hutLocation, 10);
            job.setPathingOptions(getPathingOptions());
            final Path path = job.search();
            if (path != null && path.canReach())
            {
                getResult().pond = new int[]{n.x, n.y, n.z};
                getResult().pondState = pondState;
                getResult().parent = path.getTarget();

                return true;
            }
        }

        // node is not pond -> debug
        if (problemPos != null && !problemPos.equals(new int[]{0,0,0}))
        {
            debugNodesExtra.add(new MNode(n, problemPos.getX(), problemPos.getY(), problemPos.getZ(), -1, -1));
        }

        return false;
    }

    @Override
    protected double modifyCost(
        final double cost,
        final MNode parent,
        final boolean swimstart,
        final boolean swimming,
        final int x,
        final int y,
        final int z,
        final BlockState state, final BlockState below)
    {
        if (BlockPosUtil.distSqr(hutLocation, x, y, z) > MAX_RANGE * MAX_RANGE)
        {
            return cost * 10;
        }

        return cost;
    }

    @Override
    public void setPathingOptions(final PathingOptions pathingOptions)
    {
        super.setPathingOptions(pathingOptions);
        getPathingOptions().swimCostEnter = 0;
        getPathingOptions().swimCost = 0;
    }

    @Override
    public double getEndNodeScore(final MNode n)
    {
        return BlockPosUtil.distManhattan(hutLocation, n.x, n.y, n.z);
    }

    /**
     * Simple reverse lookup to find a fitting shore for a pond location
     */
    private class PathJobFindFishingPos extends AbstractPathJob implements ISearchPathJob
    {
        private final int[] direction;
        private final int      distance;

        public PathJobFindFishingPos(
            final World actualWorld,
            final IBlockAccess world,
            final @NotNull int[] start,
            final @NotNull int[] direction,
            final int distance)
        {
            super(actualWorld, world, start, distance + 100, new PathResult(), null);
            this.direction = direction;
            this.distance = distance;
        }

        @Override
        protected void handleDebugOptions(final MNode node)
        {
            PathJobFindWater.this.handleDebugOptions(node);
        }

        @Override
        protected double computeHeuristic(final int x, final int y, final int z)
        {
            return BlockPosUtil.distManhattan(direction, x, y, z);
        }

        @Override
        protected boolean isAtDestination(final MNode n)
        {
            return !n.isSwimming()
                && BlockPosUtil.distManhattan(start, n.x, n.y, n.z) < distance
                && SurfaceType.getSurfaceType(world, cachedBlockLookup.getBlockState(n.x, n.y - 1, n.z), tempWorldPos.set(n.x, n.y - 1, n.z), getPathingOptions())
                == SurfaceType.WALKABLE && BlockUtils.isAnySolid(cachedBlockLookup.getBlockState(n.x, n.y - 1, n.z))
                && canSeeTargetFromPos(n);
        }

        /**
         * Checks visibility
         *
         * @param n
         * @return
         */
        private boolean canSeeTargetFromPos(final MNode n)
        {
            return !PathfindingUtils.hasAnyCollisionAlong(start.getX(), start.getY(), start.getZ(), n.x, n.y + 1, n.z, cachedBlockLookup);
        }

        @Override
        public double getEndNodeScore(final MNode n)
        {
            return BlockPosUtil.distManhattan(start, n.x, n.y, n.z);
        }
    }
}






