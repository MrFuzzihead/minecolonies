package net.minecraftforge.event.world;

import net.minecraft.world.World;

/** [1.7.10 bridge] LevelEvent - maps to Forge 1.7.10 WorldEvent */
public class LevelEvent
{
    private final World world;

    public LevelEvent(World w) { this.world = w; }
    public World getLevel() { return world; }

    public static class Load extends LevelEvent
    {
        public Load(World w) { super(w); }
    }

    public static class Unload extends LevelEvent
    {
        public Unload(World w) { super(w); }
    }

    public static class Save extends LevelEvent
    {
        public Save(World w) { super(w); }
    }
}

