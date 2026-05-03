package com.minecolonies.core.blocks.huts;

import com.minecolonies.api.blocks.AbstractColonyBlock;
import com.minecolonies.api.blocks.interfaces.IRSComponentBlock;
import com.minecolonies.api.colony.buildings.ModBuildings;
import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;

/**
 * Hut for the PostBox.
 * [1.7.10] Ported: setBlockBoundsBasedOnState replaces VoxelShape; getPlayerRelativeBlockHardness replaces getDestroyProgress.
 */
public class BlockPostBox extends AbstractColonyBlock<BlockPostBox> implements IRSComponentBlock
{
    @NotNull
    @Override
    public String getHutName()
    {
        return "blockpostbox";
    }

    @Override
    public BuildingEntry getBuildingEntry()
    {
        return ModBuildings.postBox.get();
    }

    @Override
    public float getPlayerRelativeBlockHardness(final EntityPlayer player, final net.minecraft.world.World world, final int x, final int y, final int z)
    {
        return 1f / 30f;
    }

    @Override
    public void setBlockBoundsBasedOnState(final IBlockAccess access, final int x, final int y, final int z)
    {
        final int meta = access.getBlockMetadata(x, y, z);
        // metadata 0=SOUTH, 1=WEST, 2=NORTH, 3=EAST
        switch (meta & 0x3)
        {
            case 2: // NORTH
                setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.5f);
                break;
            case 0: // SOUTH
                setBlockBounds(0.0f, 0.0f, 0.5f, 1.0f, 1.0f, 1.0f);
                break;
            case 3: // EAST
                setBlockBounds(0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
                break;
            case 1: // WEST
            default:
                setBlockBounds(0.0f, 0.0f, 0.0f, 0.5f, 1.0f, 1.0f);
                break;
        }
    }
}
