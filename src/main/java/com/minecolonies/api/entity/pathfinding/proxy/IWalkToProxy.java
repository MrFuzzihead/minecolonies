package com.minecolonies.api.entity.pathfinding.proxy;

// [1.7.10] int[] -> int x,y,z
// [1.7.10] world.entity removed
import net.minecraft.entity.EntityCreature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

/**
 * Interface which defines the walkToProxy.
 */
public interface IWalkToProxy
{
    /**
     * Leads the entity to a certain position due to proxies.
     *
     * @param target the position.
     * @param range  the range.
     * @return true if arrived.
     */
    boolean walkToBlock(@NotNull final int[] target, final int range);

    /**
     * Leads the entity to a certain position due to proxies.
     *
     * @param target the target position.
     * @param range  the range.
     * @param onMove entity on move or not?
     * @return true if arrived.
     */
    boolean walkToBlock(@NotNull final int[] target, final int range, final boolean onMove);

    /**
     * Get a list of waypoints depending on the entity.
     *
     * @return the set of waypoints.
     */
    Set<int[]> getWayPoints();

    /**
     * Check if for distance calculation the y World should be taken into account.
     *
     * @return true if so.
     */
    boolean careAboutY();

    /**
     * Try to get a specialized proxy to a certain target.
     *
     * @param target         the target.
     * @param distanceToPath the distance to it.
     * @return a special proxy point of existent, else null.
     */
    @Nullable
    int[] getSpecializedProxy(final int[] target, final double distanceToPath);

    /**
     * Getter for the proxyList.
     *
     * @return a copy of the list
     */
    List<int[]> getProxyList();

    /**
     * Add an entry to the proxy list.
     *
     * @param pos the position to add.
     */
    void addToProxyList(final int[] pos);

    /**
     * Method to call to detect if an entity living is at site with move.
     *
     * @param entity the entity to check.
     * @param x      the x value.
     * @param y      the y value.
     * @param z      the z value.
     * @param range  the range.
     * @return true if so.
     */
    boolean isLivingAtSiteWithMove(final EntityCreature entity, final int x, final int y, final int z, final int range);

    /**
     * Getter for the entity accociated with the proxy.
     *
     * @return the entity.
     */
    EntityCreature getEntity();

    /**
     * Getter for the current proxy.
     *
     * @return the current proxy.
     */
    int[] getCurrentProxy();

    /**
     * Reset the target of the proxy.
     */
    void reset();
}



