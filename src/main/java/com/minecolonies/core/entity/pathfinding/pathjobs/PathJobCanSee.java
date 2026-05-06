package com.minecolonies.core.entity.pathfinding.pathjobs;
import net.minecraft.world.phys.Vec3;

import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.core.entity.pathfinding.MNode;
import com.minecolonies.core.entity.pathfinding.PathfindingUtils;
import com.minecolonies.core.entity.pathfinding.SurfaceType;
import com.minecolonies.core.entity.pathfinding.pathresults.PathResult;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
// [1.7.10] world.entity removed
// [1.7.10] removed: import net.minecraft.world.World.ClipContext;
import net.minecraft.world.World;
// [1.7.10] world.phys removed
// [1.7.10] world.phys removed
import org.jetbrains.annotations.NotNull;

/**
 * Pathing job for moving into vision of the given entity
 */
public class PathJobCanSee extends AbstractPathJob implements ISearchPathJob
{
    /**
     * The entity to see
     */
    private final EntityLivingBase lookTarget;

    /**
     * The position we want to search around from, usually guarding pos, so guide the heuristic there from the current entity position
     */
    private final int[] searchAroundPos;

    public PathJobCanSee(
      final EntityCreature searchingEntity,
      final EntityLivingBase lookTarget,
      final World world,
      @NotNull final int[] searchAroundPos, final int range)
    {
        super(world, PathfindingUtils.prepareStart(searchingEntity), range, new PathResult<PathJobCanSee>(), searchingEntity);

        this.searchAroundPos = searchAroundPos;
        this.lookTarget = lookTarget;
    }

    @Override
    protected double computeHeuristic(final int x, final int y, final int z)
    {
        return BlockPosUtil.distManhattan(searchAroundPos.getX(), searchAroundPos.getY(), searchAroundPos.getZ(), x, y, z);
    }

    @Override
    protected boolean isAtDestination(final MNode n)
    {
        if (searchAroundPos.getY() - n.y > 2)
        {
            return false;
        }

        return canSeeTargetFromPos(tempWorldPos.set(n.x, n.y, n.z))
                 && SurfaceType.getSurfaceType(world, cachedBlockLookup.getBlockState(n.x, n.y - 1, n.z), tempWorldPos.set(n.x, n.y - 1, n.z), getPathingOptions())
                      == SurfaceType.WALKABLE;
    }

    /**
     * Calculate the distance to the target.
     *
     * @param n Node to test.
     * @return double of the distance.
     */
    @Override
    public double getEndNodeScore(@NotNull final MNode n)
    {
        return BlockPosUtil.distManhattan(start, n.x, n.y, n.z);
    }

    private boolean canSeeTargetFromPos(final int[] pos)
    {
        Vec3 vec3d = new Vec3(pos.getX(), pos.getY() + entity.getEyeHeight(), pos.getZ());
        Vec3 vec3d1 = new Vec3(lookTarget.getX(), lookTarget.getEyeY(), lookTarget.getZ());
        return this.world.clip(new ClipContext(vec3d, vec3d1, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity)).getType() == HitResult.Type.MISS;
    }
}



