package com.minecolonies.core.blocks;

import com.minecolonies.api.blocks.AbstractBlockMinecolonies;
import com.minecolonies.api.blocks.interfaces.ITickableBlockMinecolonies;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.items.ItemColonySign;
import com.minecolonies.core.tileentities.TileEntityColonySign;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_POS;
import static com.minecolonies.core.items.ItemColonySign.TAG_COLONY;

/**
 * Creates a colony sign block.
 * [1.7.10] Ported: Material; metadata bit 0 = CONNECTED; createTileEntity; onBlockPlacedBy; breakBlock.
 */
public class BlockColonySign extends AbstractBlockMinecolonies<BlockColonySign> implements ITickableBlockMinecolonies
{
    /**
     * Metadata bit indicating two connected colonies.
     */
    public static final int META_CONNECTED = 1;
    /** [1.7.10] BooleanProperty alias stub for renderer compat */
    public static final Object CONNECTED = null;

    private static final float  BLOCK_HARDNESS = 5F;
    private static final String BLOCK_NAME     = "colonysign";
    private static final float  RESISTANCE     = 1F;

    public BlockColonySign()
    {
        super(Material.wood);
        setHardness(BLOCK_HARDNESS);
        setResistance(RESISTANCE);
        setStepSound(Block.soundTypeWood);
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
        return new TileEntityColonySign();
    }

    @Override
    protected Class<? extends net.minecraft.item.ItemBlock> getItemClass()
    {
        return ItemColonySign.class;
    }

    @Override
    public void setBlockBoundsBasedOnState(final IBlockAccess access, final int x, final int y, final int z)
    {
        setBlockBounds(0.1f, 0.1f, 0.1f, 0.9f, 0.9f, 0.9f);
    }

    @Override
    public void onBlockPlacedBy(
      @NotNull final World worldIn,
      final int x,
      final int y,
      final int z,
      @NotNull final EntityLivingBase placer,
      final ItemStack stack)
    {
        if (worldIn.isRemote)
        {
            super.onBlockPlacedBy(worldIn, x, y, z, placer, stack);
            return;
        }

        final TileEntityColonySign tileEntityColonySign = (TileEntityColonySign) worldIn.getTileEntity(x, y, z);
        if (tileEntityColonySign == null)
        {
            return;
        }

        final NBTTagCompound stackCompound = stack.getTagCompound();
        if (stackCompound == null || !stackCompound.hasKey(TAG_COLONY))
        {
            return;
        }
        final int colonyId = stackCompound.getInteger(TAG_COLONY);
        final IColony colony = IColonyManager.getInstance().getColonyByDimension(colonyId, worldIn.provider.dimensionId);
        final int[] anchor = stackCompound.hasKey(TAG_POS) ? BlockPosUtil.read(stackCompound, TAG_POS) : null;
        tileEntityColonySign.setColonyAndAnchor(colony, anchor);
        super.onBlockPlacedBy(worldIn, x, y, z, placer, stack);
    }

    @Override
    public void breakBlock(
      final World world,
      final int x,
      final int y,
      final int z,
      final Block block,
      final int meta)
    {
        final TileEntityColonySign tileEntity = (TileEntityColonySign) world.getTileEntity(x, y, z);
        if (!world.isRemote && tileEntity != null)
        {
            final IColony colony = IColonyManager.getInstance().getColonyByDimension(tileEntity.getColonyId(), world.provider.dimensionId);
            if (colony != null)
            {
                colony.getConnectionManager().removeConnectionNode(new int[]{x, y, z});
            }
        }
        super.breakBlock(world, x, y, z, block, meta);
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
