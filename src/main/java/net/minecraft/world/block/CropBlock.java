package net.minecraft.world.block;

import net.minecraft.block.Block;

/**
 * [1.7.10] Compatibility shim for 1.21 CropBlock class.
 * In 1.7.10, crop blocks are net.minecraft.block.BlockCrops.
 */
public class CropBlock extends Block
{
    public CropBlock()
    {
        super(0, null);
    }
}

