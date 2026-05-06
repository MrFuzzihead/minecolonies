package net.minecraft.world.entity.ai.goal;

import net.minecraft.entity.EntityCreature;

import java.util.EnumSet;
import java.util.function.Predicate;

/** [1.7.10 bridge] BreakDoorGoal - maps to EntityAIBreakDoor-like behaviour */
public class BreakDoorGoal extends Goal
{
    protected final EntityCreature EntityCreature;
    protected int[] doorPos = new int[]{0, 0, 0};
    protected int breakTime = 0;
    private final Predicate<?> validDifficulty;

    public BreakDoorGoal(EntityCreature mob, Predicate<?> validDifficulty)
    {
        this.EntityCreature = mob;
        this.validDifficulty = validDifficulty;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() { return false; }

    public int getDoorBreakTime() { return 240; }
}
