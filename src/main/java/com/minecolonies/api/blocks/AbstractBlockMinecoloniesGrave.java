package com.minecolonies.api.blocks;

import com.minecolonies.api.blocks.types.GraveType;
import net.minecraft.block.material.Material;

// [1.7.10 BACKPORT] Removed EntityBlock, HorizontalDirectionalBlock, DirectionProperty, EnumProperty
// Block state properties are handled via metadata in 1.7.10

public abstract class AbstractBlockMinecoloniesGrave<B extends AbstractBlockMinecoloniesGrave<B>> extends AbstractBlockMinecolonies<B>
{
    // [1.7.10] GraveType variant stored via metadata
    // [1.7.10] FACING stored via metadata bits

    public AbstractBlockMinecoloniesGrave(final Material material)
    {
        super(material);
    }

}
