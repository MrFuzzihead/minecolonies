package com.minecolonies.api.util;

// [1.7.10] BlockState -> Block + int metadata; Property<?> not available in 1.7.10
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for handling blocks and their metadata in 1.7.10.
 * [1.7.10] Replaces the 1.21 BlockState / Property<?> API with Block + int metadata comparisons.
 */
public class BlockStateUtils
{
    private BlockStateUtils() {}

    /**
     * Checks if two block positions contain the same block and have the same metadata.
     *
     * @param world the world
     * @param x1    x of first position
     * @param y1    y of first position
     * @param z1    z of first position
     * @param x2    x of second position
     * @param y2    y of second position
     * @param z2    z of second position
     * @return true if block and metadata match
     */
    public static boolean blockAndMetaMatch(
      @NotNull final IBlockAccess world,
      final int x1, final int y1, final int z1,
      final int x2, final int y2, final int z2)
    {
        return world.getBlock(x1, y1, z1) == world.getBlock(x2, y2, z2)
                 && world.getBlockMetadata(x1, y1, z1) == world.getBlockMetadata(x2, y2, z2);
    }

    /**
     * Checks if two block+meta pairs are equal.
     */
    public static boolean blockStateEquals(
      @NotNull final Block block1, final int meta1,
      @NotNull final Block block2, final int meta2)
    {
        return block1 == block2 && meta1 == meta2;
    }

    /**
     * Checks if two block+meta pairs share the same block.
     */
    public static boolean sameBlock(@NotNull final Block b1, @NotNull final Block b2)
    {
        return b1 == b2;
    }
}
