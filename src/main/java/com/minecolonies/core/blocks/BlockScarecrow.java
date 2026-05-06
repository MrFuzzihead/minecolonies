package com.minecolonies.core.blocks;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.blocks.huts.AbstractBlockMinecoloniesDefault;
import com.minecolonies.api.blocks.interfaces.IBuildingBrowsableBlock;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildingextensions.registry.BuildingExtensionRegistries;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.client.gui.containers.WindowField;
import com.minecolonies.core.colony.buildingextensions.FarmField;
import com.minecolonies.core.tileentities.TileEntityScarecrow;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The scarecrow block — a 2-tall block marking a farm field.
 * [1.7.10] Ported: Material; metadata bit 2 = upper half, bits 0-1 = facing;
 * createTileEntity (lower half only); onBlockActivated; onBlockPlacedBy (place upper);
 * onBlockPreDestroy/breakBlock (remove partner half).
 */
@SuppressWarnings("PMD.ExcessiveImports")
public class BlockScarecrow extends AbstractBlockMinecoloniesDefault<BlockScarecrow> implements IBuildingBrowsableBlock
{
    /** Metadata bit 2 marks the upper half of the scarecrow. */
    public static final int META_UPPER_HALF = 0x4;

    private static final String BLOCK_NAME = "blockhutfield";

    public BlockScarecrow()
    {
        super(Material.wood);
        setLightOpacity(0);
    }

    @Override
    public ResourceLocation getRegistryName()
    {
        return new ResourceLocation(Constants.MOD_ID, BLOCK_NAME);
    }

    @Override
    public boolean hasTileEntity(final int metadata)
    {
        // Only the lower half has a TileEntity
        return (metadata & META_UPPER_HALF) == 0;
    }

    @Override
    public TileEntity createTileEntity(final World world, final int metadata)
    {
        if ((metadata & META_UPPER_HALF) != 0)
        {
            return null;
        }
        return new TileEntityScarecrow();
    }

    @Override
    public void setBlockBoundsBasedOnState(final IBlockAccess access, final int x, final int y, final int z)
    {
        final int meta = access.getBlockMetadata(x, y, z);
        final boolean isUpper = (meta & META_UPPER_HALF) != 0;
        setBlockBounds(
            (float) AbstractBlockMinecoloniesDefault.START_COLLISION,
            (float) (AbstractBlockMinecoloniesDefault.BOTTOM_COLLISION - (isUpper ? 1 : 0)),
            (float) AbstractBlockMinecoloniesDefault.START_COLLISION,
            (float) AbstractBlockMinecoloniesDefault.END_COLLISION,
            (float) (AbstractBlockMinecoloniesDefault.HEIGHT_COLLISION - (isUpper ? 1 : 0)),
            (float) AbstractBlockMinecoloniesDefault.END_COLLISION
        );
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
        final int meta = worldIn.getBlockMetadata(x, y, z);
        final boolean isUpper = (meta & META_UPPER_HALF) != 0;

        // Resolve to the lower half
        final int lowerX = x;
        final int lowerY = isUpper ? y - 1 : y;
        final int lowerZ = z;

        if (worldIn.isRemote)
        {
            final TileEntity entity = worldIn.getTileEntity(lowerX, lowerY, lowerZ);
            if (entity instanceof TileEntityScarecrow scarecrow)
            {
                new WindowField(scarecrow).open();
                return true;
            }
            return false;
        }

        // Server side: register farm field
        final IColony iColony = IColonyManager.getInstance().getIColony(worldIn, lowerX, lowerY, lowerZ);
        if (iColony != null)
        {
            iColony.getServerBuildingManager().addBuildingExtensionIfMissing(
                BuildingExtensionRegistries.farmField.get(),
                new int[]{lowerX, lowerY, lowerZ},
                player);
        }
        return true;
    }

    @Override
    public void onBlockPlacedBy(
      final World worldIn,
      final int x,
      final int y,
      final int z,
      final EntityLivingBase placer,
      final ItemStack stack)
    {
        super.onBlockPlacedBy(worldIn, x, y, z, placer, stack);

        // Calculate lower-half facing metadata and place upper half
        final int facingMeta = MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        worldIn.setBlockMetadataWithNotify(x, y, z, facingMeta, 2);
        worldIn.setBlock(x, y + 1, this, facingMeta | META_UPPER_HALF, 3);

        if (worldIn.isRemote)
        {
            return;
        }

        final IColony colony = IColonyManager.getInstance().getColonyByPosFromWorld(worldIn, x, y, z);
        if (colony != null)
        {
            colony.getServerBuildingManager().addBuildingExtension(FarmField.create(new int[]{x, y, z}, worldIn));
        }
    }

    @Override
    public void onBlockHarvested(final World worldIn, final int x, final int y, final int z, final int meta, final EntityPlayer player)
    {
        final boolean isUpper = (meta & META_UPPER_HALF) != 0;
        final int otherY = isUpper ? y - 1 : y + 1;
        final int otherMeta = worldIn.getBlockMetadata(x, otherY, z);

        if (worldIn.getBlock(x, otherY, z) == this && ((otherMeta & META_UPPER_HALF) != 0) != isUpper)
        {
            worldIn.setBlockToAir(x, otherY, z);
        }

        notifyColonyAboutDestruction(worldIn, x, y, z, meta);
        super.onBlockHarvested(worldIn, x, y, z, meta, player);
    }

    @Override
    public void breakBlock(final World worldIn, final int x, final int y, final int z, final Block block, final int meta)
    {
        // Handled in onBlockHarvested for player break; explosion/other causes handled here
        super.breakBlock(worldIn, x, y, z, block, meta);
    }

    private void notifyColonyAboutDestruction(final World worldIn, final int x, final int y, final int z, final int meta)
    {
        if (!worldIn.isRemote)
        {
            final boolean isUpper = (meta & META_UPPER_HALF) != 0;
            final int lowerY = isUpper ? y - 1 : y;
            final IColony colony = IColonyManager.getInstance().getColonyByPosFromWorld(worldIn, x, lowerY, z);
            if (colony != null)
            {
                final int[] fieldPos = {x, lowerY, z};
                colony.getServerBuildingManager().removeBuildingExtension(
                    field -> field.getBuildingExtensionType().equals(BuildingExtensionRegistries.farmField.get())
                                 && field.getPosition()[0] == fieldPos[0]
                                 && field.getPosition()[1] == fieldPos[1]
                                 && field.getPosition()[2] == fieldPos[2]);
            }
        }
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
