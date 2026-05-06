package com.minecolonies.core.blocks;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.blocks.AbstractBlockMinecoloniesRack;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.permissions.Action;
import com.minecolonies.api.util.InventoryUtils;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.tileentities.TileEntityRack;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Block for the shelves of the warehouse.
 * [1.7.10] Ported: Material; metadata bits 0-1 = facing, bits 2-4 = RackType;
 * createTileEntity; onBlockActivated (open rack GUI); breakBlock; double-rack
 * neighbour logic → onNeighborBlockChange (TODO: full double-rack logic).
 */
public class BlockMinecoloniesRack extends AbstractBlockMinecoloniesRack<BlockMinecoloniesRack>
{
    private static final float  BLOCK_HARDNESS = 10.0F;
    private static final String BLOCK_NAME     = "blockminecoloniesrack";
    private static final float  RESISTANCE     = Float.POSITIVE_INFINITY;

    public BlockMinecoloniesRack()
    {
        super(Material.wood);
        setHardness(BLOCK_HARDNESS);
        setResistance(RESISTANCE / 5F);
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
        return new TileEntityRack();
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
              && tileEntity instanceof TileEntityRack)
        {
            if (!worldIn.isRemote)
            {
                // TODO: Phase 9 — open rack container GUI
                // player.openGui(MineColonies.instance, MineColoniesGuiId.RACK, worldIn, x, y, z);
            }
            return true;
        }
        return false;
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
        if (tileEntity instanceof TileEntityRack)
        {
            final TileEntityRack rack = (TileEntityRack) tileEntity;
            InventoryUtils.dropItemHandler(rack.getInventory(), worldIn, rack.xCoord, rack.yCoord, rack.zCoord);
        }
        super.breakBlock(worldIn, x, y, z, block, meta);
    }

    // TODO: [1.7.10] Double-rack pairing logic (from 1.21 updateShape) needs to move here:
    //   onNeighborBlockChange: detect adjacent rack block, set metadata for EMPTY_DOUBLE / FULL_DOUBLE / NO_RENDER.
    @Override
    public void onNeighborBlockChange(final World world, final int x, final int y, final int z, final Block neighborBlock)
    {
        super.onNeighborBlockChange(world, x, y, z, neighborBlock);
        // TODO: Phase 1 — re-implement double-rack pairing via metadata bits 2-4 (RackType).
        //   When a neighboring rack block is placed/removed, update both blocks' metadata
        //   to reflect EMPTY_DOUBLE, FULL_DOUBLE, or NO_RENDER as appropriate.
    }

    @Override
    public List<ItemStack> getDrops(
      final World world,
      final int x,
      final int y,
      final int z,
      final int metadata,
      final int fortune)
    {
        final List<ItemStack> drops = new ArrayList<>();
        drops.add(new ItemStack(this, 1));
        return drops;
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
