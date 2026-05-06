package com.minecolonies.core.entity.ai.minimal;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
// [1.7.10] world.entity removed
// [1.7.10] world.entity removed

import java.util.EnumSet;

/**
 * Similar to LookAtEntityGoal, just adds movement flags
 */
public class LookAtEntityInteractGoal extends LookAtEntityGoal
{
    public LookAtEntityInteractGoal(final EntityCreature EntityCreature, final Class<? extends EntityLivingBase> lookAtType, final float lookDistance, final float probability)
    {
        super(EntityCreature, lookAtType, lookDistance, probability);
        this.setFlags(EnumSet.of(Goal.Flag.LOOK, Goal.Flag.MOVE));
    }
}



