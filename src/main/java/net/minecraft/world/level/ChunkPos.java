package net.minecraft.world.level;
/** [1.7.10] Stub for ChunkPos */
public class ChunkPos {
    public final int x, z;
    public ChunkPos(int x, int z) { this.x = x; this.z = z; }
    public ChunkPos(long packed) { this.x = (int)(packed & 0xFFFFFFFFL); this.z = (int)(packed >> 32); }
    public long toLong() { return ((long)z << 32) | (x & 0xFFFFFFFFL); }
    public static long asLong(int x, int z) { return ((long)z << 32) | (x & 0xFFFFFFFFL); }
}