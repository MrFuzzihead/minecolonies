package com.minecolonies.api.blocks;
import net.minecraft.block.material.Material;
// [1.7.10 BACKPORT] Properties -> Material
public abstract class AbstractBlockMinecoloniesContainer<B extends AbstractBlockMinecoloniesContainer<B>> extends AbstractBlockMinecolonies<B>
{
    public AbstractBlockMinecoloniesContainer(final Material material)
    {
        super(material);
    }
}
