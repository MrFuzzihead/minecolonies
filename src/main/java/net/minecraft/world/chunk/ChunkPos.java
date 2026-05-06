package net.minecraft.world.chunk;
/** [1.7.10 stub] ChunkPos */
public class ChunkPos {
    public final int x;
    public final int z;
    public ChunkPos(int x, int z) { this.x = x; this.z = z; }
    public ChunkPos(long packed) { this.x = (int)(packed & 0xFFFFFFFFL); this.z = (int)(packed >> 32 & 0xFFFFFFFFL); }
    public ChunkPos(int[] pos) { this.x = pos[0] >> 4; this.z = pos[2] >> 4; }
    public long toLong() { return ((long)x & 0xFFFFFFFFL) | (((long)z & 0xFFFFFFFFL) << 32); }
    @Override public boolean equals(Object o) { if (!(o instanceof ChunkPos)) return false; ChunkPos c = (ChunkPos)o; return x == c.x && z == c.z; }
    @Override public int hashCode() { return x * 31 + z; }
}
