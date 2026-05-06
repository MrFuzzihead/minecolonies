package com.ldtteam.structurize.blocks.interfaces;

import com.ldtteam.structurize.blueprints.v1.Blueprint;
import com.ldtteam.structurize.placement.structure.AbstractStructureHandler;
import com.ldtteam.structurize.util.PlacementSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

/** [1.7.10 stub] ISpecialCreativeHandlerAnchorBlock */
public interface ISpecialCreativeHandlerAnchorBlock extends IAnchorBlock {
    AbstractStructureHandler getStructureHandler(
        World world, int x, int y, int z,
        Blueprint blueprint, PlacementSettings placementSettings, boolean fancyPlacement);

    boolean setup(
        EntityPlayerMP player, World world, int x, int y, int z,
        Blueprint blueprint, PlacementSettings settings,
        boolean fancyPlacement, String pack, String path);
}

