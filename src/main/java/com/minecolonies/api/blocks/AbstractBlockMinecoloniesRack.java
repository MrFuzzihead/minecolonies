package com.minecolonies.api.blocks;

import com.minecolonies.api.blocks.types.RackType;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;

// [1.7.10 BACKPORT] Removed EntityBlock, HorizontalDirectionalBlock, DirectionProperty, EnumProperty
// Block state properties handled via metadata in 1.7.10

public abstract class AbstractBlockMinecoloniesRack<B extends AbstractBlockMinecoloniesRack<B>> extends AbstractBlockMinecolonies<B>
{
    // [1.7.10] RackType variant stored via metadata
    // [1.7.10] FACING stored via metadata bits

    public AbstractBlockMinecoloniesRack(final Material material)
    {
        super(material);
    }

    /**
     * Check if a certain block should be replaced with a rack.
     *
     * @param block the block to check.
     * @return true if so.
     */
    public static boolean shouldBlockBeReplacedWithRack(final Block block)
    {
        return block == Blocks.chest || block instanceof AbstractBlockMinecoloniesRack;
    }
}
