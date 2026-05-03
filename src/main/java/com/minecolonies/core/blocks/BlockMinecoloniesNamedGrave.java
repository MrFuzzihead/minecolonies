package com.minecolonies.core.blocks;

import com.minecolonies.api.blocks.AbstractBlockMinecoloniesNamedGrave;
import com.minecolonies.api.blocks.ModBlocks;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.tileentities.TileEntityNamedGrave;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Block for named graves.
 * [1.7.10] Ported: Material; metadata bits 0-1 = facing; createTileEntity; onBlockPlacedBy; canBlockStay.
 */
public class BlockMinecoloniesNamedGrave extends AbstractBlockMinecoloniesNamedGrave<BlockMinecoloniesNamedGrave>
{
    private static final float  BLOCK_HARDNESS = 5F;
    private static final String BLOCK_NAME     = "blockminecoloniesnamedgrave";
    private static final float  RESISTANCE     = 1F;

    public BlockMinecoloniesNamedGrave()
    {
        super(Material.rock);
        setHardness(BLOCK_HARDNESS);
        setResistance(RESISTANCE);
        setStepSound(Block.soundTypeStone);
    }

    @Override
    public ResourceLocation getRegistryName()
    {
        return new ResourceLocation(Constants.MOD_ID, BLOCK_NAME);
    }

    @Override
    public boolean hasTileEntity(final int metadata)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(final World world, final int metadata)
    {
        return new TileEntityNamedGrave();
    }

    @Override
    public void setBlockBoundsBasedOnState(final IBlockAccess access, final int x, final int y, final int z)
    {
        setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.1f, 1.0f);
    }

    @Override
    public void onBlockPlacedBy(
      final World worldIn,
      final int x,
      final int y,
      final int z,
      @Nullable final EntityLivingBase placer,
      final ItemStack stack)
    {
        int meta = 0;
        if (placer != null)
        {
            meta = (MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5D) + 2) & 3;
        }
        worldIn.setBlockMetadataWithNotify(x, y, z, meta, 2);
    }

    @Override
    public boolean canBlockStay(final World world, final int x, final int y, final int z)
    {
        final Block below = world.getBlock(x, y - 1, z);
        return below != ModBlocks.blockNamedGrave
                   && below.isSideSolid(world, x, y - 1, z, net.minecraftforge.common.util.ForgeDirection.UP);
    }
}
