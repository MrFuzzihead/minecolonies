package com.minecolonies.api.entity.other;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.entity.pathfinding.IStuckHandlerEntity;
import com.minecolonies.api.util.constant.ColonyConstants;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * Special abstract minecolonies EntityCreature that overrides laggy vanilla behaviour.
 * Ported from 1.21 (PathfinderMob) to 1.7.10 (EntityCreature).
 */
public abstract class AbstractFastMinecoloniesEntity extends EntityCreature implements IStuckHandlerEntity
{
    /**
     * Whether this entity can be stuck for stuckhandling
     */
    private boolean canBeStuck = true;

    /**
     * Random update variance for this entity, used to spread out updates equally
     */
    public final int randomVariance = ColonyConstants.rand.nextInt(20);

    /**
     * Cache fluid state
     */
    private boolean isInFluid = false;

    /**
     * The timepoint at which the entity last collided
     */
    private long lastHorizontalCollision = 0;

    /**
     * Last knockback time
     */
    protected long lastKnockBack = 0;

    /**
     * Create a new instance.
     *
     * @param worldIn the world.
     */
    protected AbstractFastMinecoloniesEntity(final World worldIn)
    {
        super(worldIn);
    }

    @Override
    public boolean canBeStuck()
    {
        return canBeStuck;
    }

    /**
     * Sets whether the entity currently can be stuck
     *
     * @param canBeStuck whether it can be stuck
     */
    public void setCanBeStuck(final boolean canBeStuck)
    {
        this.canBeStuck = canBeStuck;
    }

    // [1.7.10] canBeLeashedBy - not in EntityLivingBase hierarchy, no @Override
    public boolean canBeLeashedBy(final EntityPlayer player)
    {
        return false;
    }

    /**
     * Whether the citizen collided in the last 10 ticks
     *
     * @return true if recent horizontal collision
     */
    /**
     * Returns the path navigator for this entity (1.7.10 wrapper around getNavigator()).
     *
     * @return path navigator
     */
    public PathNavigate getNavigation()
    {
        return getNavigator();
    }

    public boolean hadHorizontalCollission()
    {
        return worldObj.getTotalWorldTime() - lastHorizontalCollision < 10;
    }

    /**
     * Prevent dimension changes through portals.
     */
    @Override
    public boolean handleWaterMovement()
    {
        // Noop — our entities don't do water movement optimisation per-tick; can override if needed
        return false;
    }

    @Override
    public void knockBack(final net.minecraft.entity.Entity entity, final float strength, final double xRatio, final double zRatio)
    {
        if (worldObj.getTotalWorldTime() - lastKnockBack > 20 * 3)
        {
            lastKnockBack = worldObj.getTotalWorldTime();
            super.knockBack(entity, strength, xRatio, zRatio);
        }
    }

    @Override
    public boolean attackEntityFrom(final net.minecraft.util.DamageSource dmgSource, final float dmg)
    {
        if (dmgSource.getEntity() instanceof AbstractFastMinecoloniesEntity)
        {
            final AbstractFastMinecoloniesEntity other = (AbstractFastMinecoloniesEntity) dmgSource.getEntity();
            if (other.getTeamId() == getTeamId())
            {
                return false;
            }
        }
        return super.attackEntityFrom(dmgSource, dmg);
    }

    /**
     * Get the team ID of this entity (typically colony ID).
     *
     * @return the team ID.
     */
    public abstract int getTeamId();

    // [1.7.10] canChangeDimension: not a standard override in 1.7.10, disabled
    public boolean canChangeDimension()
    {
        return false;
    }
}

