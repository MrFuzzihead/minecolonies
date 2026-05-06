package com.minecolonies.core.blocks.decorative;
import net.minecraft.world.level.block.state.BlockState;

import com.minecolonies.api.blocks.decorative.AbstractColonyFlagBanner;
import com.minecolonies.api.util.constant.Constants;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * A custom banner block to construct the associated tile entity that will render the colony flag.
 * This is the wall version. For the floor version: {@link BlockColonyFlagBanner}
 * [1.7.10] Ported: horizontal facing stored in metadata 0-3; no BlockState/VoxelShape/StateDefinition.
 * Metadata 0=SOUTH, 1=WEST, 2=NORTH, 3=EAST.
 */
public class BlockColonyFlagWallBanner extends AbstractColonyFlagBanner<BlockColonyFlagWallBanner>
{
    public BlockColonyFlagWallBanner()
    {
        super();
    }

    @Override
    public void setBlockBoundsBasedOnState(final IBlockAccess access, final int x, final int y, final int z)
    {
        final int meta = access.getBlockMetadata(x, y, z);
        switch (meta & 0x3)
        {
            case 2: // NORTH — attached to south face of block to north
                setBlockBounds(0.0f, 0.0f, 14.0f / 16.0f, 1.0f, 12.5f / 16.0f, 1.0f);
                break;
            case 0: // SOUTH — attached to north face of block to south
                setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 12.5f / 16.0f, 2.0f / 16.0f);
                break;
            case 1: // WEST — attached to east face
                setBlockBounds(14.0f / 16.0f, 0.0f, 0.0f, 1.0f, 12.5f / 16.0f, 1.0f);
                break;
            case 3: // EAST — attached to west face
            default:
                setBlockBounds(0.0f, 0.0f, 0.0f, 2.0f / 16.0f, 12.5f / 16.0f, 1.0f);
                break;
        }
    }

    @Override
    public boolean canBlockStay(final World world, final int x, final int y, final int z)
    {
        final int meta = world.getBlockMetadata(x, y, z);
        final int facing = meta & 0x3;
        // The block this banner is attached to must be solid on the face pointing toward us
        int wx = x, wz = z;
        ForgeDirection attachedSide;
        switch (facing)
        {
            case 2: wx = x;     wz = z - 1; attachedSide = ForgeDirection.SOUTH; break;
            case 0: wx = x;     wz = z + 1; attachedSide = ForgeDirection.NORTH; break;
            case 1: wx = x + 1; wz = z;     attachedSide = ForgeDirection.WEST;  break;
            default:wx = x - 1; wz = z;     attachedSide = ForgeDirection.EAST;  break;
        }
        return world.getBlock(wx, y, wz).isSideSolid(world, wx, y, wz, attachedSide);
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public ResourceLocation getRegistryName()
    {
        return new ResourceLocation(Constants.MOD_ID, REGISTRY_NAME_WALL);
    }
}
