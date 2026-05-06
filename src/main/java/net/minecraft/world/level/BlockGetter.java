package net.minecraft.world.level;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.level.material.FluidState;

/** [1.7.10 bridge] BlockGetter - maps to IBlockAccess */
public interface BlockGetter extends IBlockAccess
{
    default BlockState getBlockState(int[] pos) { return new BlockState(getBlock(pos[0], pos[1], pos[2]), getBlockMetadata(pos[0], pos[1], pos[2])); }
    default FluidState getFluidState(int[] pos) { return FluidState.EMPTY; }
}

