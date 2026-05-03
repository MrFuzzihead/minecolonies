package com.minecolonies.core.blocks;

import com.ldtteam.structurize.blocks.interfaces.IAnchorBlock;
import com.minecolonies.api.blocks.AbstractBlockMinecoloniesHorizontal;
import com.minecolonies.api.blocks.interfaces.IBuildingBrowsableBlock;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildingextensions.registry.BuildingExtensionRegistries.BuildingExtensionEntry;
import com.minecolonies.api.entity.ai.workers.util.IBuilderUndestroyable;
import com.minecolonies.api.util.Log;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.client.gui.WindowPlantationField;
import com.minecolonies.core.colony.buildingextensions.PlantationField;
import com.minecolonies.core.tileentities.TileEntityPlantationField;
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

import java.util.List;

/**
 * Block class for the plantation field block.
 * [1.7.10] Ported: Material; metadata bits 0-1 = facing, bit 2 = MIRROR;
 * createTileEntity; onBlockActivated; onBlockPlacedBy; breakBlock.
 */
public class BlockPlantationField extends AbstractBlockMinecoloniesHorizontal<BlockPlantationField>
    implements IBuilderUndestroyable, IAnchorBlock, IBuildingBrowsableBlock
{
    /** Metadata bit 2 = mirrored. */
    public static final int META_MIRROR_BIT = 0x4;

    private static final float  BLOCK_HARDNESS = 5F;
    private static final String BLOCK_NAME     = "blockhutplantationfield";
    private static final float  RESISTANCE     = 1F;

    public BlockPlantationField()
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
        return new TileEntityPlantationField();
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
        // Open plantation field GUI on client side only
        if (worldIn.isRemote)
        {
            final TileEntity tileEntity = worldIn.getTileEntity(x, y, z);
            if (tileEntity instanceof TileEntityPlantationField plantationField)
            {
                new WindowPlantationField(plantationField).open();
                return true;
            }
            return false;
        }
        return true;
    }

    @Override
    public void onBlockPlacedBy(
      @NotNull final World worldIn,
      final int x,
      final int y,
      final int z,
      final EntityLivingBase placer,
      final ItemStack stack)
    {
        super.onBlockPlacedBy(worldIn, x, y, z, placer, stack);

        if (worldIn.isRemote)
        {
            return;
        }

        final TileEntity tileEntity = worldIn.getTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityPlantationField tileEntityPlantationField)
        {
            final IColony colony = IColonyManager.getInstance().getColonyByPosFromWorld(worldIn, x, y, z);
            if (colony != null)
            {
                for (BuildingExtensionEntry plantationFieldType : tileEntityPlantationField.getPlantationFieldTypes())
                {
                    final PlantationField plantationField = PlantationField.create(plantationFieldType, new int[]{x, y, z});

                    final List<int[]> workingPositions = tileEntityPlantationField.getWorkingPositions(plantationField.getModule().getWorkTag());
                    if (workingPositions.isEmpty())
                    {
                        Log.getLogger()
                            .warn("Plantation field blueprint at path {} does not have ANY tagged working positions for the NBTBase '{}', please report this to devs!",
                                tileEntityPlantationField.getBlueprintPath(),
                                plantationField.getModule().getWorkTag());
                    }

                    final List<int[]> validPositions = plantationField.getModule().getValidWorkingPositions(worldIn, workingPositions);
                    if (!validPositions.isEmpty())
                    {
                        plantationField.setWorkingPositions(validPositions);
                        colony.getServerBuildingManager().addBuildingExtension(plantationField);
                        colony.getServerBuildingManager().addLeisureSite(new int[]{x, y, z});
                    }
                    else
                    {
                        Log.getLogger()
                            .warn("Plantation field blueprint at path {} does not have ANY VALID tagged working positions for the NBTBase '{}', please report this to devs!",
                                tileEntityPlantationField.getBlueprintPath(),
                                plantationField.getModule().getWorkTag());
                    }
                }
            }
        }
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
        notifyColonyAboutDestruction(worldIn, x, y, z);
        super.breakBlock(worldIn, x, y, z, block, meta);
    }

    @Override
    public void onBlockDestroyedByExplosion(final World worldIn, final int x, final int y, final int z, final net.minecraft.world.Explosion explosionIn)
    {
        notifyColonyAboutDestruction(worldIn, x, y, z);
        super.onBlockDestroyedByExplosion(worldIn, x, y, z, explosionIn);
    }

    @Override
    public void onBlockHarvested(final World worldIn, final int x, final int y, final int z, final int meta, final EntityPlayer player)
    {
        notifyColonyAboutDestruction(worldIn, x, y, z);
        super.onBlockHarvested(worldIn, x, y, z, meta, player);
    }

    private void notifyColonyAboutDestruction(final World worldIn, final int x, final int y, final int z)
    {
        if (!worldIn.isRemote)
        {
            final IColony colony = IColonyManager.getInstance().getColonyByPosFromWorld(worldIn, x, y, z);
            if (colony != null)
            {
                final TileEntity blockEntity = worldIn.getTileEntity(x, y, z);
                if (blockEntity instanceof TileEntityPlantationField plantationField)
                {
                    for (BuildingExtensionEntry plantationFieldType : plantationField.getPlantationFieldTypes())
                    {
                        colony.getServerBuildingManager().removeBuildingExtension(
                            field -> field.getBuildingExtensionType().equals(plantationFieldType)
                                         && field.getPosition()[0] == x
                                         && field.getPosition()[1] == y
                                         && field.getPosition()[2] == z);
                        colony.getServerBuildingManager().removeLeisureSite(new int[]{x, y, z});
                    }
                }
            }
        }
    }
}
