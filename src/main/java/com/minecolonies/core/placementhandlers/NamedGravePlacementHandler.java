package com.minecolonies.core.placementhandlers;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tileentity.BlockEntity; // [1.7.10] alias -> TileEntity

import com.ldtteam.structurize.placement.IPlacementContext;
import com.ldtteam.structurize.placement.handlers.placement.IPlacementHandler;
import com.ldtteam.structurize.util.BlockUtils;
import com.minecolonies.core.blocks.BlockMinecoloniesNamedGrave;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import com.minecolonies.api.util.Tuple;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
// [1.7.10] block.entity removed
// [1.7.10] BlockState -> int metadata
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class NamedGravePlacementHandler implements IPlacementHandler
{
    @Override
    public boolean canHandle(@NotNull final World world, @NotNull final int[] pos, @NotNull final BlockState blockState)
    {
        return blockState.getBlock() instanceof BlockMinecoloniesNamedGrave;
    }

    @Override
    public ActionProcessingResult handle(
      @NotNull final World world,
      @NotNull final int[] pos,
      @NotNull final BlockState blockState,
      @Nullable final NBTTagCompound tileEntityData,
      @NotNull final IPlacementContext placementContext)
    {
        if (!placementContext.fancyPlacement())
        {
            world.setBlockAndUpdate(pos, blockState);
            return ActionProcessingResult.SUCCESS;
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
        if (!placementContext.fancyPlacement())
        {
            return Collections.singletonList(BlockUtils.getItemStackFromBlockState(blockState));
        }
        return Collections.emptyList();
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




