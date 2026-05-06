package net.minecraft.world.chunk;

/** [1.7.10 stub] ChunkAccess - maps to Chunk. */
public class ChunkAccess
{
    private final Chunk chunk;

    public ChunkAccess(Chunk chunk) { this.chunk = chunk; }

    public ChunkPos getPos()
    {
        return chunk != null ? new ChunkPos(chunk.xPosition, chunk.zPosition) : new ChunkPos(0, 0);
    }

    public net.minecraft.tileentity.TileEntity getBlockEntity(int[] pos)
    {
        if (chunk == null) return null;
        return chunk.getTileEntity(pos[0], pos[1], pos[2]);
    }
}

