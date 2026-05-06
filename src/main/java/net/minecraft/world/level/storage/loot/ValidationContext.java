package net.minecraft.world.level.storage.loot;

import net.minecraft.util.ResourceLocation;

/** [1.7.10 bridge] ValidationContext - no 1.7.10 equivalent */
public class ValidationContext
{
    public void reportProblem(final String problem) {}
    public ValidationContext forChild(final String childName) { return this; }
}

