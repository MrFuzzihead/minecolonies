package com.minecolonies.core.entity.pathfinding.world;

import com.minecolonies.api.util.WorldUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.init.Blocks;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * A caching block reader backed by loaded chunks, used by the pathfinding system.
 * [1.7.10] reimplemented against real Chunk/IBlockAccess instead of 1.21 LevelReader.
 */
public class ChunkCache implements BlockGetter
{
    protected int     chunkX;
    protected int     chunkZ;
    protected Chunk[][] chunkArray;
    protected boolean empty;
    protected World   world;

    public ChunkCache(World worldIn, int[] posFromIn, int[] posToIn)
    {
        this.world  = worldIn;
        this.chunkX = posFromIn[0] >> 4;
        this.chunkZ = posFromIn[2] >> 4;
        int i = posToIn[0] >> 4;
        int j = posToIn[2] >> 4;
        this.chunkArray = new Chunk[i - this.chunkX + 1][j - this.chunkZ + 1];
        this.empty = true;

        for (int k = this.chunkX; k <= i; ++k)
        {
            for (int l = this.chunkZ; l <= j; ++l)
            {
                if (worldIn.getChunkProvider().chunkExists(k, l))
                {
                    Chunk chunk = worldIn.getChunkFromChunkCoords(k, l);
                    this.chunkArray[k - this.chunkX][l - this.chunkZ] = chunk;
                    if (!chunk.isEmpty())
                    {
                        this.empty = false;
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean isEmpty()
    {
        return this.empty;
    }

    // ---- IBlockAccess ----

    @Override
    public Block getBlock(final int x, final int y, final int z)
    {
        int ci = (x >> 4) - this.chunkX;
        int cj = (z >> 4) - this.chunkZ;
        if (ci >= 0 && ci < chunkArray.length && cj >= 0 && cj < chunkArray[ci].length && chunkArray[ci][cj] != null)
        {
            return chunkArray[ci][cj].getBlock(x & 15, y, z & 15);
        }
        return Blocks.air;
    }

    @Override
    public int getBlockMetadata(final int x, final int y, final int z)
    {
        int ci = (x >> 4) - this.chunkX;
        int cj = (z >> 4) - this.chunkZ;
        if (ci >= 0 && ci < chunkArray.length && cj >= 0 && cj < chunkArray[ci].length && chunkArray[ci][cj] != null)
        {
            return chunkArray[ci][cj].getBlockMetadata(x & 15, y, z & 15);
        }
        return 0;
    }

    @Override
    public TileEntity getTileEntity(final int x, final int y, final int z)
    {
        int ci = (x >> 4) - this.chunkX;
        int cj = (z >> 4) - this.chunkZ;
        if (ci >= 0 && ci < chunkArray.length && cj >= 0 && cj < chunkArray[ci].length && chunkArray[ci][cj] != null)
        {
            return chunkArray[ci][cj].func_150806_e(x & 15, y, z & 15);
        }
        return null;
    }

    @Override
    public int getLightBrightnessForSkyBlocks(final int x, final int y, final int z, final int minLight)
    {
        return world.getLightBrightnessForSkyBlocks(x, y, z, minLight);
    }

    @Override
    public boolean isAirBlock(final int x, final int y, final int z)
    {
        return getBlock(x, y, z).isAir(this, x, y, z);
    }

    @Override
    public int isBlockProvidingPowerTo(final int x, final int y, final int z, final int side)
    {
        return getBlock(x, y, z).isProvidingWeakPower(this, x, y, z, side);
    }

    // ---- BlockGetter bridge ----

    @Override
    public BlockState getBlockState(final int[] pos)
    {
        return new BlockState(getBlock(pos[0], pos[1], pos[2]), getBlockMetadata(pos[0], pos[1], pos[2]));
    }

    @Override
    public FluidState getFluidState(final int[] pos)
    {
        Block b = getBlock(pos[0], pos[1], pos[2]);
        boolean isWater = b == Blocks.water || b == Blocks.flowing_water;
        boolean isLava  = b == Blocks.lava  || b == Blocks.flowing_lava;
        return new FluidState(!isWater && !isLava);
    }

    public boolean isEmptyBlock(int[] pos)
    {
        return isAirBlock(pos[0], pos[1], pos[2]);
    }

    // Minimum/maximum build height (1.7.10 always 0–255)
    public int getMinBuildHeight() { return 0; }
    public int getMaxBuildHeight() { return 256; }
}
