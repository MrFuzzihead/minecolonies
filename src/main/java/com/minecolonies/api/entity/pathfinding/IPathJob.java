package com.minecolonies.api.entity.pathfinding;

import com.minecolonies.core.entity.pathfinding.PathingOptions;
import com.minecolonies.core.entity.pathfinding.pathresults.PathResult;
// [1.7.10] int[] -> int x,y,z
// [1.7.10] world.entity removed
import net.minecraft.world.World;
import net.minecraft.entity.EntityCreature;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.Path;

import java.util.concurrent.Callable;

/**
 * Interface for path jobs
 */
public interface IPathJob extends Callable<PathEntity>
{
    /**
     * Get the path result holder for this job
     * @return
     */
    PathResult getResult();

    /**
     * Get the pathing options used for this job
     * @return
     */
    public PathingOptions getPathingOptions();

    EntityCreature getEntity();

    World getActualWorld();

    int[] getStart();
}



