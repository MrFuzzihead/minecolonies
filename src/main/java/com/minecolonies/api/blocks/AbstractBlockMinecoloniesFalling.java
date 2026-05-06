package com.minecolonies.api.blocks;

import com.minecolonies.api.blocks.interfaces.IBlockMinecolonies;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import cpw.mods.fml.common.registry.GameRegistry;

// [1.7.10 BACKPORT] FallingBlock -> BlockFalling; Properties -> Material; IForgeRegistry -> GameRegistry

public abstract class AbstractBlockMinecoloniesFalling<B extends AbstractBlockMinecoloniesFalling<B>> extends BlockFalling implements IBlockMinecolonies<B>
{
    public AbstractBlockMinecoloniesFalling(final Material material)
    {
        super(material);
    }

    @Override
    @SuppressWarnings("unchecked")
    public B registerBlock()
    {
        // [1.7.10] registration handled via GameRegistry elsewhere
        return (B) this;
    }
}
