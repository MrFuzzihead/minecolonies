package net.minecraftforge.event.world;

import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.common.eventhandler.Event;

/** [1.7.10 stub] ChunkEvent — maps to net.minecraftforge.event.world chunk events. */
public class ChunkEvent extends Event
{
    private final Chunk chunk;

    public ChunkEvent(Chunk chunk) { this.chunk = chunk; }

    public Chunk getChunk() { return chunk; }

    public IBlockAccess getLevel() { return chunk != null ? chunk.worldObj : null; }

    /** Fired when a chunk loads. */
    public static class Load extends ChunkEvent
    {
        public Load(Chunk chunk) { super(chunk); }
    }

    /** Fired when a chunk unloads. */
    public static class Unload extends ChunkEvent
    {
        public Unload(Chunk chunk) { super(chunk); }
    }
}

