package net.minecraft.block.state;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.AxisAlignedBB;

/**
 * [1.7.10] Shim replacing the 1.21 net.minecraft.world.level.block.state.BlockState.
 * Wraps a Block + int metadata pair, providing the most-used BlockState methods
 * so the rest of the codebase needs minimal changes.
 */
public class BlockState
{
    public final Block block;
    public final int   meta;

    public BlockState(final Block block, final int meta)
    {
        this.block = block;
        this.meta  = meta;
    }

    /** Factory: create from IBlockAccess at x,y,z */
    public static BlockState of(final IBlockAccess access, final int x, final int y, final int z)
    {
        return new BlockState(access.getBlock(x, y, z), access.getBlockMetadata(x, y, z));
    }

    // ---- 1.21 API compatibility getters ----

    public Block getBlock()
    {
        return block;
    }

    public int getMeta()
    {
        return meta;
    }

    /** Equivalent of BlockState.isAir() */
    public boolean isAir()
    {
        return block == null || block.isAir(null, 0, 0, 0);
    }

    /** Rough equivalent of BlockState.isSolid() – uses 1.7.10 opaque-cube check */
    public boolean isSolid()
    {
        return block != null && block.isOpaqueCube();
    }

    /** Whether this block can be replaced (grass, water, etc.) */
    public boolean canBeReplaced()
    {
        return block == null || block.isReplaceable(null, 0, 0, 0);
    }

    /** Stub for getFluidState() – no equivalent in 1.7.10 */
    public Object getFluidState()
    {
        return null;
    }

    /** [1.7.10] Stub for getCollisionShape – returns full block AABB */
    public AxisAlignedBB getCollisionShape(final IBlockAccess world, final int[] pos)
    {
        return AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 1, 1);
    }

    /** Stub for getShape() */
    public Object getShape(final IBlockAccess world, final int x, final int y, final int z)
    {
        return null;
    }

    /** Checks if this state's block matches the given block */
    public boolean is(final Block other)
    {
        return block == other;
    }

    /** Stub for Tag-based is() check – always false in 1.7.10 */
    public boolean is(final Object tag)
    {
        return false;
    }

    /**
     * Stub for getValue(property) – returns null.
     * Callers that truly need metadata-encoded properties should read {@link #meta} directly.
     */
    public Object getValue(final Object property)
    {
        return null;
    }

    /** Stub for setValue(property, value) – returns this (immutable shim) */
    public BlockState setValue(final Object property, final Object value)
    {
        return this;
    }

    /** isRedstoneConductor – delegates to block */
    public boolean isRedstoneConductor(final IBlockAccess world, final int x, final int y, final int z)
    {
        return block != null && block.isProvidingStrongPower(world, x, y, z, 0) > 0;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof BlockState))
        {
            return false;
        }
        final BlockState that = (BlockState) o;
        return meta == that.meta && block == that.block;
    }

    @Override
    public int hashCode()
    {
        return 31 * (block == null ? 0 : block.hashCode()) + meta;
    }

    @Override
    public String toString()
    {
        return "BlockState{block=" + block + ", meta=" + meta + "}";
    }
}



