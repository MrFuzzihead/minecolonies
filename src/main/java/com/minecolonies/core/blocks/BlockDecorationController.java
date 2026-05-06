package com.minecolonies.core.blocks;
import net.minecraft.world.entity.player.Player;

import com.ldtteam.structurize.blocks.interfaces.IAnchorBlock;
import com.ldtteam.structurize.blocks.interfaces.ILeveledBlueprintAnchorBlock;
import com.minecolonies.api.blocks.AbstractBlockMinecoloniesDirectional;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.entity.ai.workers.util.IBuilderUndestroyable;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.client.gui.WindowDecorationController;
import com.minecolonies.core.tileentities.TileEntityDecorationController;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;

import static com.ldtteam.structurize.blockentities.interfaces.IBlueprintDataProviderBE.TAG_BLUEPRINTDATA;
import static com.ldtteam.structurize.blockentities.interfaces.IBlueprintDataProviderBE.TAG_SCHEMATIC_NAME;

/**
 * Creates a decoration controller block.
 * [1.7.10] Ported: Material; metadata bits 0-2 = facing (6 directions), bit 3 = MIRROR;
 * createTileEntity; onBlockActivated; onBlockPlacedBy.
 * WATERLOGGED removed (no fluid logging in 1.7.10).
 */
public class BlockDecorationController extends AbstractBlockMinecoloniesDirectional<BlockDecorationController>
    implements IBuilderUndestroyable, IAnchorBlock, ILeveledBlueprintAnchorBlock
{
    private static final float  BLOCK_HARDNESS = 5F;
    private static final String BLOCK_NAME     = "decorationcontroller";
    private static final float  RESISTANCE     = 1F;

    /** Metadata bit 3 = mirrored. */
    public static final int META_MIRROR_BIT = 0x8;

    public BlockDecorationController()
    {
        super(Material.wood);
        setHardness(BLOCK_HARDNESS);
        setResistance(RESISTANCE);
        setStepSound(Block.soundTypeWood);
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
        return true;
    }

    @Override
    public TileEntity createTileEntity(final World world, final int metadata)
    {
        return new TileEntityDecorationController();
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
        if (worldIn.isRemote)
        {
            final TileEntity tileEntity = worldIn.getTileEntity(x, y, z);
            if (tileEntity instanceof TileEntityDecorationController)
            {
                new WindowDecorationController(new int[]{x, y, z}).open();
            }
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
        if (tileEntity instanceof TileEntityDecorationController)
        {
            final TileEntityDecorationController controller = (TileEntityDecorationController) tileEntity;
            // Check for leisure tag at origin position
            if (controller.getPositionedTags().getOrDefault(new int[]{0, 0, 0}, new ArrayList<>()).contains("leisure"))
            {
                @Nullable final IColony colony = IColonyManager.getInstance().getColonyByPosFromWorld(worldIn, x, y, z);
                if (colony != null)
                {
                    colony.getServerBuildingManager().addLeisureSite(new int[]{x, y, z});
                }
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

    @Override
    public int getLevel(final NBTTagCompound beData)
    {
        if (beData == null)
        {
            return 0;
        }
        try
        {
            return Integer.parseInt(beData.getCompoundTag(TAG_BLUEPRINTDATA).getString(TAG_SCHEMATIC_NAME).replaceAll("[^0-9]", ""));
        }
        catch (final NumberFormatException exception)
        {
            return 0;
        }
    }
}
