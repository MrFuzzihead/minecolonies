package com.minecolonies.api.util;

// [1.7.10] BlockState -> Block + int metadata; Property<?> not available
import net.minecraft.block.Block;
import org.jetbrains.annotations.NotNull;

/**
 * Stores a block and metadata for comparing.
 * [1.7.10] Replaces 1.21 BlockState + Property<?> pattern with Block + int metadata.
 */
public class BlockStateStorage
{
    /** The block to store. */
    private final Block block;

    /** The metadata to store. */
    private final int meta;

    /** Hashcode of the storage. */
    private final int hashCode;

    /**
     * Create an instance of the storage.
     *
     * @param block the block
     * @param meta  the block metadata
     */
    public BlockStateStorage(@NotNull final Block block, final int meta)
    {
        this.block    = block;
        this.meta     = meta;
        this.hashCode = 31 * block.hashCode() + meta;
    }

    /** Returns the stored block. */
    public Block getBlock() { return block; }

    /** Returns the stored metadata. */
    public int getMeta() { return meta; }

    @Override
    public int hashCode() { return hashCode; }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o) return true;
        if (!(o instanceof BlockStateStorage other)) return false;
        return meta == other.meta && block == other.block;
    }
}
