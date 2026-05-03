package com.minecolonies.core.entity.pathfinding.pathjobs;

import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.core.entity.pathfinding.MNode;
import com.minecolonies.core.entity.pathfinding.PathingOptions;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * Custom path job to connect signs.
 */
public class PathJobSignConnection extends PathJobMoveToLocation implements IDestinationPathJob
{
    public PathJobSignConnection(final World world, @NotNull final int[] start, final int[] end, final int range)
    {
        super(world, start, end, range, null);
        maxNodes = 20000;
        setPathingOptions(new PathingOptions().withCanSwim(true).withCanEnterDoors(true).withCanEnterGates(true));
    }

    @Override
    protected boolean isAtDestination(@NotNull final MNode n)
    {
        return BlockPosUtil.distSqr(destination.getX(), destination.getY(), destination.getZ(), n.x, destination.getY(), n.z) <= 4;
    }
}


