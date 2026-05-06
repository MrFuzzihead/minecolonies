package com.minecolonies.api.compatibility.candb;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
// [1.7.10] BlockState → int blockMeta
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * This class is to store a check to see if a block is a chiselsandbits block.
 */
public final class ChiselAndBitsCheck extends AbstractChiselAndBitsProxy
{
    /**
     * Check if tileEntity is candb block.
     *
     * @param tileEntity the tileEntity.
     * @return if the tileEntity is a candb tileEntity.
     */
    public static boolean isChiselAndBitsTileEntity(@NotNull final TileEntity tileEntity)
    {
        return new ChiselAndBitsCheck().checkForChiselAndBitsTileEntity(tileEntity);
    }

    /**
     * Check if a block meta is a candb chiseled block.
     *
     * @param blockMeta the block metadata.
     * @return if the block is a candb block.
     */
    public static boolean isChiselAndBitsBlock(final int blockMeta)
    {
        return new ChiselAndBitsCheck().checkForChiselAndBitsBlock(blockMeta);
    }

    /**
     * Get candb bits as a list of itemStacks from tileEntity..
     *
     * @param tileEntity the tileEntity.
     * @return the list of itemStacks..
     */
    public static List<ItemStack> getBitStacks(final TileEntity tileEntity)
    {
        return new ChiselAndBitsCheck().getChiseledStacks(tileEntity);
    }

    @Override
    public boolean checkForChiselAndBitsBlock(final int blockMeta)
    {
        return false;
    }

    @Override
    public boolean checkForChiselAndBitsTileEntity(@NotNull final TileEntity tileEntity)
    {
        return false;
    }

    @Override
    public List<ItemStack> getChiseledStacks(@NotNull final TileEntity tileEntity)
    {
        return Collections.emptyList();
    }
}

