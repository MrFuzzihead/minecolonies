package com.minecolonies.core.blocks;

import com.minecolonies.api.blocks.AbstractBlockMinecolonies;
import com.minecolonies.api.blocks.interfaces.ITickableBlockMinecolonies;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.tileentities.TileEntityCompostedDirt;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;
import org.jetbrains.annotations.NotNull;

/**
 * Block that if activated with BoneMeal or Compost by an AI will produce flowers by intervals until it deactivates.
 * [1.7.10] Ported: Material replaces Properties; createTileEntity/hasTileEntity replaces newBlockEntity;
 * canSustainPlant signature updated for 1.7.10.
 */
public class BlockCompostedDirt extends AbstractBlockMinecolonies<BlockCompostedDirt> implements ITickableBlockMinecolonies
{
    private static final String BLOCK_NAME     = "composted_dirt";
    private static final float  BLOCK_HARDNESS = 5f;
    private static final float  RESISTANCE     = 1f;

    public BlockCompostedDirt()
    {
        super(Material.ground);
        setHardness(BLOCK_HARDNESS);
        setResistance(RESISTANCE);
        setStepSound(Block.soundTypeGravel);
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
        return new TileEntityCompostedDirt();
    }

    @Override
    public boolean canSustainPlant(
      @NotNull final IBlockAccess world,
      final int x,
      final int y,
      final int z,
      @NotNull final ForgeDirection direction,
      final IPlantable plantable)
    {
        return true;
    }
}
