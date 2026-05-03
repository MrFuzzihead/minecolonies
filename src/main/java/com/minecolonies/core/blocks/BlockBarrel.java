package com.minecolonies.core.blocks;

import com.minecolonies.api.blocks.AbstractBlockMinecoloniesHorizontal;
import com.minecolonies.api.blocks.interfaces.ITickableBlockMinecolonies;
import com.minecolonies.api.blocks.types.BarrelType;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.tileentities.TileEntityBarrel;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * Block for the barrel (compost/items).
 * [1.7.10] Ported: Material; metadata bits 0-1 = facing, bits 2-4 = BarrelType;
 * createTileEntity; onBlockActivated; setBlockBoundsBasedOnState; canBlockStay.
 */
public class BlockBarrel extends com.minecolonies.api.blocks.AbstractBlockBarrel<BlockBarrel> implements ITickableBlockMinecolonies
{
    private static final float  BLOCK_HARDNESS = 5F;
    private static final String BLOCK_NAME     = "barrel_block";
    private static final float  RESISTANCE     = 1F;

    public BlockBarrel()
    {
        super(Material.wood);
        setHardness(BLOCK_HARDNESS);
        setResistance(RESISTANCE);
        setStepSound(Block.soundTypeWood);
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
        return new TileEntityBarrel();
    }

    @Override
    public boolean onBlockActivated(
      final World worldIn,
      final int x,
      final int y,
      final int z,
      final EntityPlayer player,
      final int side,
      final float hitX,
      final float hitY,
      final float hitZ)
    {
        final ItemStack itemstack = player.getHeldItem();
        final TileEntity te = worldIn.getTileEntity(x, y, z);
        if (te instanceof TileEntityBarrel && !worldIn.isRemote)
        {
            ((TileEntityBarrel) te).useBarrel(player, itemstack, side);
            ((TileEntityBarrel) te).updateBlock(worldIn);
        }
        return true;
    }

    @Override
    public void setBlockBoundsBasedOnState(final IBlockAccess access, final int x, final int y, final int z)
    {
        // Barrel is slightly taller than a normal block (1.5 height); clamp at 1.0 for 1.7.10 bounds
        setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
    }

    @Override
    public boolean canBlockStay(final World world, final int x, final int y, final int z)
    {
        final Block below = world.getBlock(x, y - 1, z);
        return below != com.minecolonies.api.blocks.ModBlocks.blockBarrel
                   && below.isSideSolid(world, x, y - 1, z, net.minecraftforge.common.util.ForgeDirection.UP);
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }
}
