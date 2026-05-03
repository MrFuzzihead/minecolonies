package com.minecolonies.api.compatibility.dynamictrees;

// [1.7.10 BACKPORT] DynamicTrees 1.21 packages do not exist in 1.7.10.
// This class is stubbed out. A full DynamicTrees 1.7.10 integration would need
// the correct 1.7.10 DynamicTrees API imports.
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public final class DynamicTreeCompat extends DynamicTreeProxy
{
    public DynamicTreeCompat() {}

    @Override public boolean isDynamicTreePresent() { return true; }
    @Override public boolean checkForDynamicTreeBlock(@NotNull final Block block) { return false; }
    @Override public boolean checkForDynamicLeavesBlock(@NotNull final Block block) { return false; }
    @Override public boolean checkForDynamicTrunkShellBlock(final Block block) { return false; }

    @Override
    public java.util.List<ItemStack> getDropsForLeaf(
      final IBlockAccess world, final int[] pos, final int blockMeta, final int fortune, final Block leaf)
    {
        return Collections.emptyList();
    }

    @Override public boolean checkForDynamicSapling(@NotNull final Item item) { return false; }

    @Override
    public Runnable getTreeBreakActionCompat(@NotNull final World world, @NotNull final int[] blockToBreak, final ItemStack toolToUse, final int[] workerPos)
    {
        return () -> {};
    }

    @Override
    public boolean plantDynamicSaplingCompat(@NotNull final World world, @NotNull final int[] location, @NotNull final ItemStack saplingStack)
    {
        return false;
    }

    @Override public int getDynamicTreeDamage() { return 0; }

    @Override
    public boolean hasFittingTreeFamilyCompat(@NotNull final int[] block1, @NotNull final int[] block2, @NotNull final IBlockAccess world)
    {
        return false;
    }
}

