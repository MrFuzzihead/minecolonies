package com.minecolonies.api.compatibility.dynamictrees;
import net.minecraft.world.level.block.state.BlockState;

// [1.7.10] int[] -> int x,y,z
import java.util.List;
// [1.7.10] int /* ResourceKey */ -> int dimensionId
// [1.7.10] net.minecraft.util.DamageSource removed
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.Block;
// [1.7.10] int /*BlockState*/ -> int metadata

/**
 * This is the fallback for when dynamictrees is not present!
 */
public class DynamicTreeProxy
{
    /**
     * Default method for when dynamic Tree's mod is not present, returns false
     *
     * @return true if so.
     */
    public boolean isDynamicTreePresent()
    {
        return false;
    }

    /**
     * Returns the damageType string falling dynamic Tree's use
     *
     * @return damageType
     */
    public int getDynamicTreeDamage()
    {
        return 0;
    }

    /**
     * Default method for when dynamic Tree's mod is not present, returns false
     *
     * @param block Block to check
     * @return false
     */
    public boolean checkForDynamicTreeBlock(final Block block)
    {
        return false;
    }

    /**
     * Default method for when dynamic Tree's mod is not present, returns false
     *
     * @param block Block to check
     * @return false
     */
    public boolean checkForDynamicLeavesBlock(final Block block)
    {
        return false;
    }

    /**
     * Default method for when dynamic Tree's mod is not present, returns false.
     *
     * @param block Block to check
     * @return false
     */
    public boolean checkForDynamicTrunkShellBlock(final Block block)
    {
        return false;
    }

    /**
     * Get the list of Drops from a Dynamic leaf
     *
     * @param leaf       The leaf to check
     * @param world      the world it is in.
     * @param pos        the pos of the block.
     * @param blockMeta the block metadata to check.
     * @param fortune    the fortune effect.
     * @return {@link java.util.List} of {@link ItemStack} Drops
     */
    public java.util.List<ItemStack> getDropsForLeaf(
      final IBlockAccess world,
      final int[] pos,
      final int blockMeta,
      final int fortune,
      final Block leaf)
    {return java.util.Collections.emptyList();}

    /**
     * Default method for when dynamic Tree's mod is not present, returns false
     *
     * @param item Block to check
     * @return false
     */
    public boolean checkForDynamicSapling(final Item item)
    {
        return false;
    }

    /**
     * Default method when dynamic tree's isnt present
     *
     * @param world        the world it is in.
     * @param blockToBreak the block position.
     * @param toolToUse    the tool
     * @param workerPos    the pos of the worker
     * @return Null
     */
    public Runnable getTreeBreakActionCompat(final World world, final int[] blockToBreak, final ItemStack toolToUse, final int[] workerPos) {return null;}

    /**
     * Default method for trying to plant a dynamic sapling when the mod isnt present.
     *
     * @param world    the world it is in.
     * @param location the position.
     * @param sapling  the sapling stack.
     * @return false
     */
    public boolean plantDynamicSaplingCompat(final World world, final int[] location, final ItemStack sapling) {return false;}

    /**
     * Default method to check if two given blocks have the same Tree family
     *
     * @param block1 First int[] to compare
     * @param block2 Second int[] to compare
     * @param world  the world it is in.
     * @return if compat exists.
     */
    public boolean hasFittingTreeFamilyCompat(final int[] block1, final int[] block2, final IBlockAccess world) {return false;}
}




