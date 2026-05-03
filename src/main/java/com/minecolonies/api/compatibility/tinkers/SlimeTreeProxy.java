package com.minecolonies.api.compatibility.tinkers;

import net.minecraft.block.Block;
// [1.7.10] int /*BlockState*/ -> int metadata
import org.jetbrains.annotations.NotNull;

/**
 * This is the fallback for when tinkers is not present!
 */
public class SlimeTreeProxy
{
    /**
     * This is the fallback for when tinkers is not present!
     *
     * @param block the block.
     * @return if the block is a slime block.
     */
    public boolean checkForTinkersSlimeBlock(@NotNull final Block block)
    {
        return false;
    }

    /**
     * This is the fallback for when tinkers is not present!
     *
     * @param block the block.
     * @return if the block is a slime leaf.
     */
    public boolean checkForTinkersSlimeLeaves(@NotNull final Block block)
    {
        return false;
    }

    /**
     * This is the fallback for when tinkers is not present!
     *
     * @param block the block.
     * @return if the block is a slime sapling.
     */
    public boolean checkForTinkersSlimeSapling(@NotNull final Block block)
    {
        return false;
    }

    /**
     * This is the fallback for when tinkers is not present!
     *
     * @param block the block.
     * @return if the block is a slime sapling.
     */
    public boolean checkForTinkersSlimeDirtOrGrass(@NotNull final Block block)
    {
        return false;
    }

    public int getTinkersLeafVariant(@NotNull final int /*BlockState*/ leaf)
    {
        return 0;
    }
}

