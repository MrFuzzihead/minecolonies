package net.minecraft.world.entity.ai.goal;

import net.minecraft.entity.ai.EntityAIBase;

import java.util.EnumSet;

/** [1.7.10 bridge] Goal - maps to EntityAIBase */
public abstract class Goal extends EntityAIBase
{
    public enum Flag { MOVE, LOOK, JUMP, TARGET }

    private EnumSet<Flag> flags = EnumSet.noneOf(Flag.class);

    // Bridge EntityAIBase abstract methods to Goal API
    @Override
    public final boolean shouldExecute() { return canUse(); }

    @Override
    public boolean continueExecuting() { return canContinueToUse(); }

    @Override
    public void startExecuting() { start(); }

    @Override
    public void resetTask() { stop(); }

    @Override
    public void updateTask() { tick(); }

    // Goal API
    public abstract boolean canUse();

    public boolean canContinueToUse() { return canUse(); }

    public void start() {}

    public void stop() {}

    public void tick() {}

    public void setFlags(EnumSet<Flag> flags) { this.flags = flags; }

    public EnumSet<Flag> getFlags() { return flags; }

    protected int adjustedTickDelay(int ticks) { return ticks; }
}
