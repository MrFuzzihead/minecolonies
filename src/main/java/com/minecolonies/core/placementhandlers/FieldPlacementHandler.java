package com.minecolonies.core.placementhandlers;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tileentity.BlockEntity; // [1.7.10] alias -> TileEntity

import com.ldtteam.structurize.placement.IPlacementContext;
import com.ldtteam.structurize.placement.handlers.placement.IPlacementHandler;
import com.ldtteam.structurize.util.BlockUtils;
import com.minecolonies.api.util.Log;
import com.minecolonies.core.blocks.BlockScarecrow;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import com.minecolonies.api.util.Tuple;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.level.block.DoorBlock;
// [1.7.10] block.entity removed
// [1.7.10] BlockState -> int metadata
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.ldtteam.structurize.placement.handlers.placement.PlacementHandlers.handleTileEntityPlacement;

public class FieldPlacementHandler implements IPlacementHandler
{
    @Override
    public boolean canHandle(@NotNull World world, @NotNull int[] pos, @NotNull BlockState blockState)
    {
        return blockState.getBlock() instanceof BlockScarecrow;
    }

    @Override
    public ActionProcessingResult handle(
      @NotNull World world,
      @NotNull int[] pos,
      @NotNull BlockState blockState,
      @Nullable NBTTagCompound tileEntityData,
      @NotNull final IPlacementContext placementContext)
    {
        if (blockState.getValue(DoorBlock.HALF).equals(DoubleBlockHalf.LOWER))
        {
            world.setBlock(pos, blockState.setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER), 3);
            world.setBlock(pos.above(), blockState.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), 3);
        }

        if (tileEntityData != null)
        {
            try
            {
                handleTileEntityPlacement(tileEntityData, world, pos, placementContext.getRotationMirror());
                blockState.getBlock().setPlacedBy(world, pos, blockState, null, BlockUtils.getItemStackFromBlockState(blockState));
            }
            catch (final Exception ex)
            {
                Log.getLogger().warn("Unable to place TileEntity");
            }
        }

        return ActionProcessingResult.SUCCESS;
    }

    @Override
    public List<ItemStack> getRequiredItems(
        @NotNull World world,
        @NotNull int[] pos,
        @NotNull BlockState blockState,
        @Nullable NBTTagCompound tileEntityData,
        @NotNull final IPlacementContext placementContext)
    {
        List<ItemStack> itemList = new ArrayList<>();
        if (blockState.getValue(DoorBlock.HALF).equals(DoubleBlockHalf.LOWER))
        {
            itemList.add(BlockUtils.getItemStackFromBlockState(blockState));
        }

        return itemList;
    }

    @Override
    public boolean doesWorldStateMatchBlueprintState(
        final BlockState blueprintState,
        final BlockState worldState,
        final Tuple<BlockEntity, NBTTagCompound> tuple,
        @NotNull final IPlacementContext iPlacementContext)
    {
        return blueprintState.getBlock() == worldState.getBlock();
    }
}




