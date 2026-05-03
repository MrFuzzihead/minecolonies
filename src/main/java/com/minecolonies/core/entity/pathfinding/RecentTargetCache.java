package com.minecolonies.core.entity.pathfinding;

// [1.7.10] int[] -> int x,y,z

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Small target cache class, which tracks used target positions for some time to have citizens spread out more naturally when a target was recently used.
 */
public class RecentTargetCache
{
    /**
     * Max amount removed per cleanup call
     */
    private static final int  REMOVALS    = 64;
    /**
     * Path targets expire after 200 seconds
     */
    private static final long EXPIRE_TIME = 1000 * 200;

    /**
     * Storage for position, expire time and cost
     */
    private static class TargetInfo
    {
        int[] pos;
        long     expiresAt;
        double   extraCost;
    }

    private static final Map<int[], TargetInfo>         cache    = new ConcurrentHashMap<>();
    private static final ConcurrentLinkedQueue<TargetInfo> expiries = new ConcurrentLinkedQueue<>();

    /**
     * Adds a position that got used as path target
     *
     * @param pos
     * @param extraCost extra cost assigned to further paths resulting in this pos
     */
    public static void add(int[] pos, double extraCost)
    {
        TargetInfo targetWithTIme = new TargetInfo();
        targetWithTIme.expiresAt = System.currentTimeMillis() + EXPIRE_TIME;
        targetWithTIme.extraCost = extraCost;
        targetWithTIme.pos = pos;

        cache.put(pos, targetWithTIme);
        expiries.add(targetWithTIme);
    }

    /**
     * Get the extra cost associated with ending a path at this int[]
     *
     * @param pos
     * @return
     */
    public static double getExtraCost(int[] pos)
    {
        cleanup();
        TargetInfo targetInfo = cache.get(pos);
        if (targetInfo == null)
        {
            return 0.0;
        }
        return targetInfo.extraCost;
    }

    /**
     * Cleans up older entries
     */
    private static void cleanup()
    {
        final long nowMillis = System.currentTimeMillis();
        for (int i = 0; i < REMOVALS; i++)
        {
            TargetInfo targetInfo = expiries.peek();
            if (targetInfo == null || targetInfo.expiresAt > nowMillis)
            {
                break;
            }

            TargetInfo polled = expiries.poll();
            if (polled == null)
            {
                break;
            }

            cache.remove(polled.pos, polled);
        }
    }
}


