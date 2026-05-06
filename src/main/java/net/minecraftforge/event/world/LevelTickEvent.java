package net.minecraftforge.event.world;

import net.minecraft.world.World;

/** [1.7.10 stub] LevelTickEvent - maps to world tick events */
public class LevelTickEvent
{
    public final net.minecraft.world.World world;

    public LevelTickEvent(final World world) { this.world = world; }

    /** [1.7.10] getLevel() maps to the world field */
    public World getLevel() { return world; }
}
