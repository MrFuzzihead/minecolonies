package net.minecraft.world.item;

import net.minecraft.item.Item;

/**
 * [1.7.10] Compatibility shim for 1.13+ BlockItem.
 * In 1.7.10, block-items are just Items. This shim allows 1.21 code to compile.
 */
public class BlockItem extends Item
{
    public BlockItem(final Object block, final Object properties)
    {
        super();
    }
}

