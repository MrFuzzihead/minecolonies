package com.minecolonies.core.entity.ai.minimal;

import com.minecolonies.api.util.CompatibilityUtils;
import com.minecolonies.api.util.Log;
import com.minecolonies.core.entity.pathfinding.navigation.MinecoloniesAdvancedPathNavigate;
import com.minecolonies.core.entity.pathfinding.pathjobs.PathJobEscapeWater;
import com.minecolonies.core.entity.pathfinding.pathresults.PathResult;
import net.minecraft.entity.EntityCreature;
import net.minecraft.world.entity.ai.goal.FloatGoal;
// [1.7.10] int[] -> int x,y,z
// [1.7.10] world.entity removed
// [1.7.10] world.entity removed
// [1.7.10] world.entity removed

/**
 * Custom float goal, which bumps less(only every 3 ticks) and does trigger escape pathfinding underwater
 */
public class EntityAIFloat extends FloatGoal
{
    /**
     * Owner of the goal
     */
    private final EntityCreature owner;

    /**
     * Water pathfinding result
     */
    private PathResult waterPathing = null;

    public EntityAIFloat(final EntityCreature EntityCreature)
    {
        super(EntityCreature);
        owner = EntityCreature;

        if (!(EntityCreature.getNavigation() instanceof MinecoloniesAdvancedPathNavigate))
        {
            Log.getLogger().error("Unsupported entity for EntityAIFloat goal:" + EntityCreature);
        }
    }

    @Override
    public void tick()
    {
        if (!owner.getEyeInFluidType().isAir() && owner.getEyeInFluidType().canSwim(owner))
        {
            if (owner.worldObj.getBlock((int)owner.posX, (int)owner.posY + 1, (int)owner.posZ).isAir(owner.worldObj, (int)owner.posX, (int)owner.posY + 1, (int)owner.posZ))
            {
                if (owner.tickCount % 3 == 0)
                {
                    owner.getJumpControl().jump();
                }
                return;
            }
            if (waterPathing == null || !waterPathing.isInProgress())
            {
                if (owner.getNavigation() instanceof MinecoloniesAdvancedPathNavigate nav)
                {
                    nav.setPauseTicks(0);
                    nav.stop();

                    waterPathing = nav.setPathJob(
                      new PathJobEscapeWater(CompatibilityUtils.getWorldFromEntity(owner),
                        owner.blockPosition(),
                        (int) owner.getAttribute(Attributes.FOLLOW_RANGE).getValue() * 5,
                        owner),
                      null, 1.0, false);
                    nav.setPauseTicks(20 * 15);
                }
            }
        }
        else
        {
            if (waterPathing != null)
            {
                waterPathing = null;
                if (owner.getNavigation() instanceof MinecoloniesAdvancedPathNavigate nav)
                {
                    nav.setPauseTicks(0);
                }
            }

            if (owner.tickCount % 3 == 0)
            {
                owner.getJumpControl().jump();
            }
        }
    }
}



