package com.ldtteam.domumornamentum.block.decorative;

import net.minecraft.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/** [1.7.10 stub] PanelBlock */
public class PanelBlock extends Block
{
    public static final BooleanProperty OPEN = BooleanProperty.create("open");

    public PanelBlock() { super(net.minecraft.block.material.Material.wood); }
}

