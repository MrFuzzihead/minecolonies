package com.minecolonies.api.entity.pathfinding;

// [1.7.10] world.entity removed
import net.minecraft.pathfinding.PathNavigate;

/**
 * Stuck handler for pathing, gets called to check/deal with stuck status
 */
public interface IStuckHandler<NAV extends PathNavigate & IMinecoloniesNavigator>
{
    /**
     * Checks if the navigator is stuck
     *
     * @param navigator navigator to check
     */
    void checkStuck(final NAV navigator);

    void resetGlobalStuckTimers();

    /**
     * Returns the stuck World (0-9) indicating how long the entity is stuck and which stuck actions got used
     *
     * @return
     */
    public int getStuckLevel();
}


