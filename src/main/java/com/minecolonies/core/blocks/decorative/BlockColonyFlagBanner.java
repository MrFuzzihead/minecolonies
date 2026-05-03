package com.minecolonies.core.blocks.decorative;

import com.minecolonies.api.blocks.decorative.AbstractColonyFlagBanner;
import com.minecolonies.api.util.constant.Constants;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * A custom banner block to construct the associated tile entity that will render the colony flag.
 * This is the floor version. For the wall version: {@link BlockColonyFlagWallBanner}
 * [1.7.10] Ported: rotation stored in metadata 0-15; no BlockState/VoxelShape/StateDefinition.
 */
public class BlockColonyFlagBanner extends AbstractColonyFlagBanner<BlockColonyFlagBanner>
{
    public BlockColonyFlagBanner()
    {
        super();
        setBlockBounds(4.0f / 16.0f, 0.0f, 4.0f / 16.0f, 12.0f / 16.0f, 1.0f, 12.0f / 16.0f);
    }

    @Override
    public void setBlockBoundsBasedOnState(final IBlockAccess access, final int x, final int y, final int z)
    {
        setBlockBounds(4.0f / 16.0f, 0.0f, 4.0f / 16.0f, 12.0f / 16.0f, 1.0f, 12.0f / 16.0f);
    }

    /**
     * Returns the rotation metadata (0-15) to use when placed.
     * Matches 1.21 logic: floor((180 + yaw) * 16 / 360 + 0.5) & 15.
     */
    public static int getRotationForYaw(final float rotation)
    {
        return MathHelper.floor_double((double) ((180.0F + rotation) * 16.0F / 360.0F) + 0.5D) & 15;
    }

    @Override
    public int onBlockPlaced(
      final World world,
      final int x,
      final int y,
      final int z,
      final int side,
      final float hitX,
      final float hitY,
      final float hitZ,
      final int meta)
    {
        // meta will be set by the placer; rotation is computed from player yaw at placement time
        return meta;
    }

    @Override
    public boolean canBlockStay(final World world, final int x, final int y, final int z)
    {
        final net.minecraft.block.Block below = world.getBlock(x, y - 1, z);
        return below.isSideSolid(world, x, y - 1, z, net.minecraftforge.common.util.ForgeDirection.UP);
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
        return new ResourceLocation(Constants.MOD_ID, REGISTRY_NAME);
    }
}
