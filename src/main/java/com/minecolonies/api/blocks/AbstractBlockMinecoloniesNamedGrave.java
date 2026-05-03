package com.minecolonies.api.blocks;
import net.minecraft.block.material.Material;
// [1.7.10 BACKPORT] Removed EntityBlock, HorizontalDirectionalBlock, DirectionProperty
public abstract class AbstractBlockMinecoloniesNamedGrave<B extends AbstractBlockMinecoloniesNamedGrave<B>> extends AbstractBlockMinecolonies<B>
{
    // [1.7.10] FACING stored via metadata
    public AbstractBlockMinecoloniesNamedGrave(final Material material)
    {
        super(material);
    }
}
