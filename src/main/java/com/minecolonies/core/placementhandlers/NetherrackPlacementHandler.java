package com.minecolonies.core.placementhandlers;

import com.ldtteam.structurize.placement.IPlacementContext;
import com.ldtteam.structurize.placement.handlers.placement.IPlacementHandler;
import com.minecolonies.api.util.Tuple;
// [1.7.10] block.entity removed
// [1.7.10] BlockState -> int metadata
import net.minecraft.init.Blocks;
import net.minecraft.world.level.block.NyliumBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class NetherrackPlacementHandler implements IPlacementHandler
{
    @Override
    public boolean canHandle(@NotNull World world, @NotNull int[] pos, @NotNull BlockState blockState)
    {
        return blockState.getBlock() instanceof NyliumBlock;
    }

    @Override
    public ActionProcessingResult handle(
      @NotNull World world,
      @NotNull int[] pos,
      @NotNull BlockState blockState,
      @Nullable NBTTagCompound tileEntityData,
      @NotNull final IPlacementContext placementContext)
    {
        return !world.setBlock(pos, blockState, 3) ? ActionProcessingResult.DENY : ActionProcessingResult.SUCCESS;
    }

    @Override
    public List<ItemStack> getRequiredItems(@NotNull World world,
        @NotNull int[] pos,
        @NotNull BlockState blockState,
        @Nullable NBTTagCompound tileEntityData,
        @NotNull final IPlacementContext placementContext)
    {
        List<ItemStack> itemList = new ArrayList<>();
        if (placementContext.fancyPlacement())
        {
            itemList.add(new ItemStack(Blocks.NETHERRACK));
        }
        else
        {
            itemList.add(new ItemStack(blockState.getBlock()));
        }

        return itemList;
    }

    @Override
    public boolean doesWorldStateMatchBlueprintState(
        final BlockState worldState,
        final BlockState blueprintState,
        final Tuple<BlockEntity, NBTTagCompound> blockEntityData,
        @NotNull final IPlacementContext placementContext)
    {
        if (placementContext.fancyPlacement())
        {
            return worldState.getBlock() instanceof NyliumBlock || worldState.getBlock() == Blocks.NETHERRACK;
        }
        return worldState.equals(blueprintState);
    }
}




