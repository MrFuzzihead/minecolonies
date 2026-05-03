package com.minecolonies.core.blocks.decorative;

import com.minecolonies.api.blocks.decorative.AbstractBlockMinecoloniesConstructionTape;
import com.minecolonies.api.util.constant.Constants;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * This block is used as a border to show the size of the building.
 * [1.7.10] Ported: VoxelShape/BlockState/FluidState removed; connections stored in metadata bits 0-3
 * (NORTH=0x1, EAST=0x2, SOUTH=0x4, WEST=0x8). FACING, CORNER, WATERLOGGED removed.
 * updateShape → onNeighborBlockChange; makeShapes → setBlockBoundsBasedOnState stub.
 */
public class BlockConstructionTape extends AbstractBlockMinecoloniesConstructionTape<BlockConstructionTape>
{
    private static final String BLOCK_NAME = "blockconstructiontape";

    /** Metadata connection flags. */
    public static final int META_NORTH = 0x1;
    public static final int META_EAST  = 0x2;
    public static final int META_SOUTH = 0x4;
    public static final int META_WEST  = 0x8;

    public BlockConstructionTape()
    {
        super(Material.cloth);
        setHardness(0.0f);
        setStepSound(Block.soundTypeCloth);
        setLightOpacity(0);
    }

    @Override
    public ResourceLocation getRegistryName()
    {
        return new ResourceLocation(Constants.MOD_ID, BLOCK_NAME);
    }

    @Override
    public void setBlockBoundsBasedOnState(final IBlockAccess access, final int x, final int y, final int z)
    {
        // Thin tape block — 2/16 wide in each direction
        setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 2.0f / 16.0f, 1.0f);
    }

    @Override
    public void onNeighborBlockChange(final World world, final int x, final int y, final int z, final Block neighborBlock)
    {
        super.onNeighborBlockChange(world, x, y, z, neighborBlock);
        if (world.isRemote)
        {
            return;
        }
        // Recompute connection flags from neighbors
        int meta = 0;
        if (isConstructionTape(world, x, y, z - 1)) meta |= META_NORTH;
        if (isConstructionTape(world, x + 1, y, z)) meta |= META_EAST;
        if (isConstructionTape(world, x, y, z + 1)) meta |= META_SOUTH;
        if (isConstructionTape(world, x - 1, y, z)) meta |= META_WEST;

        // If isolated, default to NORTH+SOUTH connection (east-west axis)
        if (meta == 0)
        {
            meta = META_NORTH | META_SOUTH;
        }
        else if (Integer.bitCount(meta) == 1)
        {
            // Single connection: extend in both directions
            meta |= Integer.reverse(meta);
        }

        world.setBlockMetadataWithNotify(x, y, z, meta, 2);
    }

    private static boolean isConstructionTape(final IBlockAccess world, final int x, final int y, final int z)
    {
        return world.getBlock(x, y, z) instanceof BlockConstructionTape;
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
    public int getRenderType()
    {
        return -1; // invisible / custom render
    }
}
