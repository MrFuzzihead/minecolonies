package com.minecolonies.core.entity.pathfinding.world;

// [1.7.10] int[] -> int x,y,z
import net.minecraft.world.IBlockAccess;
// [1.7.10] ChunkAccess/ChunkStatus/BlockGetter/LevelReader removed; using IBlockAccess directly
import org.jetbrains.annotations.Nullable;

/**
 * Small block lookup cache, to avoid repeated lookups.
 * [1.7.10] Reimplemented using IBlockAccess (Block + metadata) instead of BlockState objects.
 */
public class CachingBlockLookup implements IBlockAccess
{
    private final static int SIZE         = 5;
    private final static int MIDDLEOFFSET = SIZE / 2;

    private int centerX;
    private int centerY;
    private int centerZ;

    private final IBlockAccess world;

    private BlockState[] states   = new BlockState[SIZE * SIZE * SIZE];
    private BlockState[] exchange = new BlockState[SIZE * SIZE * SIZE];

    public CachingBlockLookup(final int[] center, final IBlockAccess world)
    {
        centerX = center[0] + MIDDLEOFFSET;
        centerY = center[1] + MIDDLEOFFSET;
        centerZ = center[2] + MIDDLEOFFSET;
        this.world = world;
    }

    public BlockState getBlockState(final int[] pos)
    {
        return getBlockState(pos[0], pos[1], pos[2]);
    }

    public BlockState getBlockState(final int x, final int y, final int z)
    {
        final int xPos = centerX - x;
        final int yPos = centerY - y;
        final int zPos = centerZ - z;

        if (xPos < 0 || xPos >= SIZE || yPos < 0 || yPos >= SIZE || zPos < 0 || zPos >= SIZE)
        {
            return BlockState.of(world, x, y, z);
        }

        final int index = xPos + yPos * SIZE + zPos * SIZE * SIZE;
        BlockState state = states[index];
        if (state == null)
        {
            state = BlockState.of(world, x, y, z);
            states[index] = state;
        }
        return state;
    }

    public void resetToNextPos(final int x, final int y, final int z)
    {
        final int xDiff = (x + MIDDLEOFFSET) - centerX;
        final int yDiff = (y + MIDDLEOFFSET) - centerY;
        final int zDiff = (z + MIDDLEOFFSET) - centerZ;

        if (Math.abs(xDiff) >= SIZE || Math.abs(yDiff) >= SIZE || Math.abs(zDiff) >= SIZE)
        {
            for (int i = 0; i < states.length; i++) { states[i] = null; }
        }
        else
        {
            for (int i = 0; i < states.length; i++)
            {
                final BlockState state = states[i];
                if (state != null)
                {
                    states[i] = null;
                    int zPos = i / (SIZE * SIZE);
                    int yPos = (i - (zPos * SIZE * SIZE)) / SIZE;
                    int xPos = (i - (zPos * SIZE * SIZE) - (yPos * SIZE));
                    zPos += zDiff; yPos += yDiff; xPos += xDiff;
                    if (xPos < 0 || xPos >= SIZE || yPos < 0 || yPos >= SIZE || zPos < 0 || zPos >= SIZE) { continue; }
                    exchange[xPos + yPos * SIZE + zPos * SIZE * SIZE] = state;
                }
            }
            final BlockState[] tmp = states; states = exchange; exchange = tmp;
        }

        centerX = x + MIDDLEOFFSET;
        centerY = y + MIDDLEOFFSET;
        centerZ = z + MIDDLEOFFSET;
    }

    // ---- IBlockAccess implementation ----

    @Override
    public net.minecraft.block.Block getBlock(final int x, final int y, final int z)
    {
        return getBlockState(x, y, z).block;
    }

    @Override
    public net.minecraft.tileentity.TileEntity getTileEntity(final int x, final int y, final int z)
    {
        return null;
    }

    @Override
    public int getBlockMetadata(final int x, final int y, final int z)
    {
        return getBlockState(x, y, z).meta;
    }

    @Override
    public int isBlockProvidingPowerTo(final int x, final int y, final int z, final int side)
    {
        return 0;
    }

    @Override
    public boolean isAirBlock(final int x, final int y, final int z)
    {
        return getBlock(x, y, z).isAir(this, x, y, z);
    }

    @Override
    public net.minecraft.world.biome.BiomeGenBase getBiomeGenForCoords(final int x, final int z)
    {
        return world.getBiomeGenForCoords(x, z);
    }

    @Override
    public int getHeight()
    {
        return world.getHeight();
    }

    @Override
    public boolean extendedLevelsInChunkCache()
    {
        return false;
    }

    @Override
    public boolean isSideSolid(final int x, final int y, final int z, final net.minecraft.util.Direction side, final boolean def)
    {
        return def;
    }
}
