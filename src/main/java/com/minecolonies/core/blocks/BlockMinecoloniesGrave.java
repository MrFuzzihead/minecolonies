package com.minecolonies.core.blocks;

import com.minecolonies.api.blocks.AbstractBlockMinecoloniesGrave;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.permissions.Action;
import com.minecolonies.api.util.InventoryUtils;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.tileentities.TileEntityGrave;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * Block for the graves.
 * [1.7.10] Ported: Material; metadata bits 0-1 = facing, bit 2 = GraveType;
 * createTileEntity; onBlockActivated; onBlockPlacedBy; breakBlock.
 */
public class BlockMinecoloniesGrave extends AbstractBlockMinecoloniesGrave<BlockMinecoloniesGrave>
{
    private static final float  BLOCK_HARDNESS = 1.5F;
    private static final String BLOCK_NAME     = "blockminecoloniesgrave";
    private static final float  RESISTANCE     = 5F;

    public BlockMinecoloniesGrave()
    {
        super(Material.rock);
        setHardness(BLOCK_HARDNESS);
        setResistance(RESISTANCE);
        setStepSound(Block.soundTypeStone);
        setLightOpacity(0);
        setBlockBounds(0.1f, 0.1f, 0.1f, 0.9f, 0.9f, 0.9f);
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
        return new TileEntityGrave();
    }

    @Override
    public void setBlockBoundsBasedOnState(final IBlockAccess access, final int x, final int y, final int z)
    {
        setBlockBounds(0.1f, 0.1f, 0.1f, 0.9f, 0.9f, 0.9f);
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
        final IColony colony = IColonyManager.getInstance().getColonyByPosFromWorld(worldIn, x, y, z);
        final TileEntity tileEntity = worldIn.getTileEntity(x, y, z);

        if ((colony == null || colony.getPermissions().hasPermission(player, Action.ACCESS_HUTS))
              && tileEntity instanceof TileEntityGrave)
        {
            if (!worldIn.isRemote)
            {
                // TODO: Phase 9 — open grave container GUI
                // player.openGui(MineColonies.instance, MineColoniesGuiId.GRAVE, worldIn, x, y, z);
            }
            return true;
        }
        return false;
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
        int meta = 0; // default GraveType.DEFAULT
        if (placer != null)
        {
            // Encode facing in bits 0-1
            meta = MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
            // Facing is opposite of placer direction: (meta + 2) & 3
            meta = (meta + 2) & 3;
        }
        worldIn.setBlockMetadataWithNotify(x, y, z, meta, 2);
    }

    @Override
    public void breakBlock(
      final World worldIn,
      final int x,
      final int y,
      final int z,
      final Block block,
      final int meta)
    {
        final TileEntity tileEntity = worldIn.getTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityGrave)
        {
            InventoryUtils.dropItemHandler(((TileEntityGrave) tileEntity).getInventory(), worldIn, x, y, z);
        }
        super.breakBlock(worldIn, x, y, z, block, meta);
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
}
