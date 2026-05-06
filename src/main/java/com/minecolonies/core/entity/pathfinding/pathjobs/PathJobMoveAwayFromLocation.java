package com.minecolonies.core.entity.pathfinding.pathjobs;
import net.minecraft.block.state.BlockState;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.core.entity.pathfinding.MNode;
import com.minecolonies.core.entity.pathfinding.PathingOptions;
import com.minecolonies.core.entity.pathfinding.SurfaceType;
import com.minecolonies.core.entity.pathfinding.pathresults.PathResult;
// [1.7.10] int[] -> int x,y,z
// [1.7.10] world.entity removed
import net.minecraft.world.World;
import net.minecraft.entity.EntityCreature;
// [1.7.10] BlockState -> int metadata
import org.jetbrains.annotations.NotNull;

/**
 * Job that handles moving away from something.
 */
public class PathJobMoveAwayFromLocation extends AbstractPathJob implements IDestinationPathJob
{
    /**
     * Position to run to, in order to avoid something.
     */
    @NotNull
    protected final int[] avoid;
    /**
     * Required avoidDistance.
     */
    protected final int      avoidDistance;

    /**
     * The blockposition we're trying to move away to
     */
    private int[] preferredDirection;

    /**
     * Prepares the PathJob for the path finding system.
     *
     * @param world         world the entity is in.
     * @param start         starting location.
     * @param avoid         location to avoid.
     * @param avoidDistance how far to move away.
     * @param range         max range to search.
     * @param entity        the entity.
     */
    public PathJobMoveAwayFromLocation(
      final World world,
      @NotNull final int[] start,
      @NotNull final int[] avoid,
      final int avoidDistance,
      final int range,
      final EntityCreature entity)
    {
        super(world, start, range, new PathResult<PathJobMoveAwayFromLocation>(), entity);

        this.avoid = new int[]{avoid[0], avoid[1], avoid[2]};
        this.avoidDistance = avoidDistance;

        // [1.7.10] Use entity position components directly instead of blockPosition()/subtract()/multiply()
        final int ex = (int) entity.posX;
        final int ey = (int) entity.posY;
        final int ez = (int) entity.posZ;
        final int dx = ex - avoid[0];
        final int dy = ey - avoid[1];
        final int dz = ez - avoid[2];
        preferredDirection = new int[]{ex + dx * range, ey + dy * range, ez + dz * range};
        if (entity instanceof AbstractEntityCitizen)
        {
            final IColony colony = ((AbstractEntityCitizen) entity).getCitizenColonyHandler().getColonyOrRegister();
            if (colony != null)
            {
                preferredDirection = colony.getCenter();
            }
        }
    }

    /**
     * For MoveAwayFromLocation we want our heuristic to weight.
     *
     * @return heuristic as a double - Manhatten Distance with tie-breaker.
     */
    @Override
    protected double computeHeuristic(final int x, final int y, final int z)
    {
        return BlockPosUtil.dist(preferredDirection, x, y, z);
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
        if (BlockPosUtil.dist(avoid, x, y, z) < 3)
        {
            return cost + 100;
        }

        return cost;
    }

    /**
     * Checks if the destination has been reached. Meaning that the avoid distance has been reached.
     *
     * @param n Node to test.
     * @return true if so.
     */
    @Override
    protected boolean isAtDestination(@NotNull final MNode n)
    {
        return BlockPosUtil.dist(avoid, n.x, n.y, n.z) > avoidDistance
                   && SurfaceType.getSurfaceType(world, cachedBlockLookup.getBlockState(n.x, n.y - 1, n.z), setPos(n.x, n.y - 1, n.z), getPathingOptions())
                      == SurfaceType.WALKABLE;
    }

    /**
     * Calculate the distance to the target.
     *
     * @param n Node to test.
     * @return double amount.
     */
    @Override
    protected double getEndNodeScore(@NotNull final MNode n)
    {
        return -BlockPosUtil.dist(avoid, n.x, n.y, n.z);
    }

    @Override
    public void setPathingOptions(final PathingOptions pathingOptions)
    {
        super.setPathingOptions(pathingOptions);
        pathingOptions.dropCost = 5;
    }

    @Override
    public int[] getDestination()
    {
        return preferredDirection;
    }

    /**
     * Helper to compare if the given move away job matches the input parameters
     *
     * @return true if the given job is the same
     */
    public static boolean isJobFor(final AbstractPathJob job, final int avoidDistance, final int[] toAvoid)
    {
        if (job instanceof PathJobMoveAwayFromLocation pathJob)
        {
            return pathJob.avoidDistance == avoidDistance && pathJob.avoid.equals(toAvoid);
        }

        return false;
    }
}




