package com.minecolonies.core.blocks.huts;

import com.minecolonies.api.blocks.AbstractBlockHut;
import com.minecolonies.api.colony.buildings.ModBuildings;
import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;

/**
 * Hut for the graveyard.
 * [1.7.10] Ported: VoxelShape removed; setBlockBoundsBasedOnState used instead.
 */
public class BlockHutGraveyard extends AbstractBlockHut<BlockHutGraveyard>
{
    public BlockHutGraveyard()
    {
        super();
    }

    @Override
    public void setBlockBoundsBasedOnState(final IBlockAccess access, final int x, final int y, final int z)
    {
        setBlockBounds(0.1f, 0.1f, 0.1f, 0.9f, 1.0f, 0.9f);
    }

    @NotNull
    @Override
    public String getHutName()
    {
        return "blockhutgraveyard";
    }

    @Override
    public BuildingEntry getBuildingEntry()
    {
        return ModBuildings.graveyard.get();
    }
}
