package com.minecolonies.core.placementhandlers;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tileentity.BlockEntity; // [1.7.10] alias -> TileEntity

import com.ldtteam.structurize.api.util.ItemStackUtils;
import com.ldtteam.structurize.placement.IPlacementContext;
import com.ldtteam.structurize.placement.handlers.placement.IPlacementHandler;
import com.ldtteam.structurize.placement.handlers.placement.PlacementHandlers;
import com.ldtteam.structurize.util.BlockUtils;
import com.minecolonies.api.blocks.ModBlocks;
import com.minecolonies.core.blocks.BlockMinecoloniesRack;
import com.minecolonies.api.util.Tuple;
// [1.7.10] block.entity removed
// [1.7.10] BlockState -> int metadata
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.ldtteam.structurize.placement.handlers.placement.PlacementHandlers.handleTileEntityPlacement;
import static com.minecolonies.api.util.constant.Constants.UPDATE_FLAG;

public class RackPlacementHandler implements IPlacementHandler
{
    @Override
    public boolean canHandle(@NotNull final World world, @NotNull final int[] pos, @NotNull final BlockState blockState)
    {
        return blockState.getBlock() instanceof BlockMinecoloniesRack;
    }

    @Override
    public ActionProcessingResult handle(
      @NotNull final World world,
      @NotNull final int[] pos,
      @NotNull final BlockState blockState,
      @Nullable final NBTTagCompound tileEntityData,
      @NotNull final IPlacementContext placementContext)
    {
        world.setBlock(pos, blockState, UPDATE_FLAG);
        if (tileEntityData != null)
        {
            handleTileEntityPlacement(tileEntityData, world, pos, placementContext.getRotationMirror());
        }
        return ActionProcessingResult.SUCCESS;
    }

    @Override
    public List<ItemStack> getRequiredItems(
      @NotNull final World world,
      @NotNull final int[] pos,
      @NotNull final BlockState blockState,
      @Nullable final NBTTagCompound tileEntityData,
        @NotNull final IPlacementContext placementContext)
    {
        final List<ItemStack> itemList = new ArrayList<>();
        if (world.getBlockState(pos).getBlock() == ModBlocks.blockRack && placementContext.fancyPlacement())
        {
            return itemList;
        }

        itemList.add(BlockUtils.getItemStackFromBlockState(blockState));
        for (final ItemStack stack : PlacementHandlers.getItemsFromTileEntity(tileEntityData, blockState))
        {
            if (!ItemStackUtils.isEmpty(stack))
            {
                itemList.add(stack);
            }
        }
        return itemList;
    }

    @Override
    public boolean doesWorldStateMatchBlueprintState(
        final BlockState worldState,
        final BlockState blueprintState,
        final Tuple<BlockEntity, NBTTagCompound> blockEntityData,
        @NotNull final IPlacementContext structureHandler)
    {
        return worldState.getBlock() == blueprintState.getBlock();
    }
}




