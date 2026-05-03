package com.minecolonies.api.entity.mobs;

// [1.7.10] Enemy interface does not exist in 1.7.10.
public interface IRangedMobEntity
{
    /**
     * Modifier to ranged attack delays
     *
     * @return higher = longer delay, shorter = less delay
     */
    default double getAttackDelayModifier()
    {
        return 1;
    }

    /**
     * If the shooter penetrates fluids.
     * @return true if so.
     */
    default boolean penetrateFluids() { return true; }
}
