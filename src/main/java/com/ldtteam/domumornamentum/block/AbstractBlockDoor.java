package com.ldtteam.domumornamentum.block;

import net.minecraft.block.Block;

/** [1.7.10 stub] AbstractBlockDoor - domumornamentum door block base */
public abstract class AbstractBlockDoor<T extends AbstractBlockDoor<T>> extends Block
{
    protected AbstractBlockDoor(net.minecraft.block.material.Material material)
    {
        super(material);
    }
}

