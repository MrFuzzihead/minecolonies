package com.ldtteam.domumornamentum.block.vanilla;

import net.minecraft.block.Block;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/** [1.7.10 stub] TrapdoorBlock - domumornamentum vanilla trapdoor */
public class TrapdoorBlock extends Block
{
    public static final BooleanProperty OPEN = BooleanProperty.create("open");

    public TrapdoorBlock() { super(net.minecraft.block.material.Material.wood); }
}

