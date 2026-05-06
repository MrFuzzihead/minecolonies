package com.minecolonies.core.placementhandlers;
import net.minecraft.world.level.block.state.BlockState;

import com.minecolonies.api.blocks.decorative.AbstractBlockGate;
// [1.7.10] BlockState -> int metadata
// [1.7.10] int[] -> int x,y,z
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * Placement handler for special gate blocks
 */
public class GatePlacementHandler extends GeneralBlockPlacementHandler
{
    @Override
    public boolean canHandle(@NotNull final World world, @NotNull final int[] pos, @NotNull final BlockState blockState)
    {
        return blockState.getBlock() instanceof AbstractBlockGate;
    }
}


