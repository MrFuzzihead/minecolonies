package com.minecolonies.api.blocks.decorative;

import com.minecolonies.api.blocks.AbstractBlockMinecoloniesFalling;
import net.minecraft.block.material.Material;
import net.minecraft.util.EnumFacing;

// [1.7.10 BACKPORT] Removed VoxelShape, BlockState, FluidState, BooleanProperty, DirectionProperty, SimpleWaterloggedBlock
// Construction tape uses metadata-based connection flags in 1.7.10

public abstract class AbstractBlockMinecoloniesConstructionTape<B extends AbstractBlockMinecoloniesConstructionTape<B>> extends AbstractBlockMinecoloniesFalling<B>
{
    // [1.7.10] Connection flags: NORTH=1, EAST=2, SOUTH=4, WEST=8 stored in metadata

    public AbstractBlockMinecoloniesConstructionTape(final Material material)
    {
        super(material);
    }

    protected int getIndex(final int metadata)
    {
        return metadata & 0xF;
    }
}
