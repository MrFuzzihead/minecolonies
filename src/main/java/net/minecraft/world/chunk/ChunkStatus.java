package net.minecraft.world.chunk;

/** [1.7.10 stub] ChunkStatus. */
public class ChunkStatus
{
    public static final ChunkStatus FULL = new ChunkStatus("full");
    private final String name;
    private ChunkStatus(String name) { this.name = name; }
}

