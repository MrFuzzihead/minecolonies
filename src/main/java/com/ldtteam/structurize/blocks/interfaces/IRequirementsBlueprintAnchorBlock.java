package com.ldtteam.structurize.blocks.interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import java.util.List;

/** [1.7.10 stub] IRequirementsBlueprintAnchorBlock */
public interface IRequirementsBlueprintAnchorBlock extends IAnchorBlock {
    List<String> getRequirements(World world, int x, int y, int z, EntityPlayer player);
    boolean areRequirementsMet(World world, int x, int y, int z, EntityPlayer player);
}

