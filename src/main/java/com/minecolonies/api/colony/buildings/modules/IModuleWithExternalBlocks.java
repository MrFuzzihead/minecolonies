package com.minecolonies.api.colony.buildings.modules;
import net.minecraft.world.level.block.state.BlockState;

// [1.7.10] int[] -> int x,y,z
import net.minecraft.world.World;
// [1.7.10] BlockState -> int metadata
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Module type to register specific blocks to a building (beds, workstations, etc).
 */
public interface IModuleWithExternalBlocks extends IBuildingModule
{
    /**
     * Attempt to register a specific block at a specific module.
     * @param blockState the state.
     * @param pos the position.
     * @param world the world.
     */
    void onBlockPlacedInBuilding(int blockMeta, @NotNull int[] pos, @NotNull World world);

    /**
     * Get the list of registered blocks.
     * @return the list of positions of the blocks.
     */
    List<int[]> getRegisteredBlocks();
}


