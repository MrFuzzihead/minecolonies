package net.minecraft.world.entity.ai.goal;

/** [1.7.10 stub] FloatGoal - maps to EntityAISwimming */
public class FloatGoal extends Goal
{
    protected final net.minecraft.entity.EntityCreature mob;

    public FloatGoal(net.minecraft.entity.EntityCreature mob)
    {
        this.mob = mob;
    }

    @Override
    public boolean canUse()
    {
        return mob.isInWater();
    }

    @Override
    public void tick()
    {
        if (mob.getRNG().nextFloat() < 0.8F)
        {
            mob.getJumpHelper().setJumping();
        }
    }
}

