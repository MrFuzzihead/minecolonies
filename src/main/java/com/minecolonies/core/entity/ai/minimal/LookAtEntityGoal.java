package com.minecolonies.core.entity.ai.minimal;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.util.WorldUtil;
import com.minecolonies.api.util.constant.ColonyConstants;
import com.minecolonies.core.colony.jobs.AbstractJobGuard;
import com.minecolonies.core.entity.citizen.EntityCitizen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.entity.ai.goal.Goal;
// [1.7.10] world.entity removed
// [1.7.10] world.entity removed
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class LookAtEntityGoal extends Goal
{
    public static final float                         DEFAULT_PROBABILITY = 0.02F;
    protected final     EntityCreature                           EntityCreature;
    @Nullable
    protected           Entity                        lookAt;
    protected final     float                         lookDistance;
    private             int                           lookTime;
    protected final     float                         probability;
    private final       boolean                       onlyHorizontal;
    protected final     Class<? extends EntityLivingBase> lookAtType;

    public LookAtEntityGoal(EntityCreature EntityCreature, Class<? extends EntityLivingBase> lookAtType, float lookDistance)
    {
        this(EntityCreature, lookAtType, lookDistance, DEFAULT_PROBABILITY);
    }

    public LookAtEntityGoal(EntityCreature EntityCreature, Class<? extends EntityLivingBase> lookAtType, float lookDistance, float probability)
    {
        this(EntityCreature, lookAtType, lookDistance, probability, false);
    }

    public LookAtEntityGoal(EntityCreature EntityCreature, Class<? extends EntityLivingBase> lookAtType, float lookDistance, float probability, boolean p_148122_)
    {
        this.EntityCreature = EntityCreature;
        this.lookAtType = lookAtType;
        this.lookDistance = lookDistance;
        this.probability = probability;
        this.onlyHorizontal = p_148122_;
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse()
    {
        if (ColonyConstants.rand.nextFloat() >= this.probability)
        {
            return false;
        }
        else
        {
            if (this.EntityCreature.getTarget() != null)
            {
                this.lookAt = this.EntityCreature.getTarget();
            }

            if (this.lookAtType == Player.class)
            {
                this.lookAt = WorldUtil.getNearestPlayer(this.EntityCreature, this.EntityCreature.getBlockX(), this.EntityCreature.getBlockY() + 1, this.EntityCreature.getBlockZ(), lookDistance);
            }
            else
            {
                this.lookAt = WorldUtil.getNearestEntity(this.EntityCreature.World.getEntitiesOfClass(this.lookAtType,
                  this.EntityCreature.getBoundingBox().inflate(this.lookDistance, 3.0D, this.lookDistance),
                  (entity) -> true), this.EntityCreature, this.EntityCreature.getBlockX(), this.EntityCreature.getBlockY() + 1, this.EntityCreature.getBlockZ(), lookDistance);
            }

            if (EntityCreature instanceof EntityCitizen citizen && citizen.getCitizenJobHandler().getColonyJob() instanceof AbstractJobGuard<?> job && job.isAsleep())
            {
                return false;
            }

            return this.lookAt != null;
        }
    }

    @Override
    public boolean canContinueToUse()
    {
        if (!this.lookAt.isAlive())
        {
            return false;
        }
        else if (this.EntityCreature.distanceToSqr(this.lookAt) > (double) (this.lookDistance * this.lookDistance))
        {
            return false;
        }
        else
        {
            return this.lookTime > 0;
        }
    }

    @Override
    public void start()
    {
        this.lookTime = this.adjustedTickDelay(40 + this.EntityCreature.getRandom().nextInt(40));
    }

    @Override
    public void stop()
    {
        this.lookAt = null;
    }

    @Override
    public void tick()
    {
        if (this.lookAt.isAlive())
        {
            double d0 = this.onlyHorizontal ? this.EntityCreature.getEyeY() : this.lookAt.getEyeY();
            this.EntityCreature.getLookControl().setLookAt(this.lookAt.getX(), d0, this.lookAt.getZ());
            --this.lookTime;
        }
    }
}



