package net.minecraft.world.block;

import net.minecraft.block.Block;

/**
 * [1.7.10] Compatibility shim for 1.21 WallBlock.
 * In 1.7.10 this is net.minecraft.block.BlockWall.
 */
public class WallBlock extends Block
{
    public WallBlock()
    {
        super(0, null);
    }
}

