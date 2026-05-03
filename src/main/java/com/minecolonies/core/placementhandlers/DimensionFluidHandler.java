package com.minecolonies.core.placementhandlers;

import com.ldtteam.structurize.placement.IPlacementContext;
import com.ldtteam.structurize.placement.handlers.placement.IPlacementHandler;
import com.ldtteam.structurize.util.BlockUtils;
import com.minecolonies.api.util.WorldUtil;
import com.minecolonies.api.util.Tuple;
// [1.7.10] block.entity removed
// [1.7.10] BlockState -> int metadata
import net.minecraft.init.Blocks;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ldtteam.structurize.api.util.constant.Constants.UPDATE_FLAG;

/**
 * Makes lava in the nether free and water everywhere else.
 */
public class DimensionFluidHandler implements IPlacementHandler
{
    @Override
    public boolean canHandle(@NotNull World world, @NotNull int[] pos, @NotNull BlockState blockState)
    {
         return blockState.getBlock() instanceof LiquidBlock || blockState.getBlock() instanceof BubbleColumnBlock;
    }

    @Override
    public List<ItemStack> getRequiredItems(
      @NotNull World world,
      @NotNull int[] pos,
      @NotNull BlockState blockState,
      @Nullable NBTTagCompound tileEntityData,
      @NotNull final IPlacementContext placementContext)
    {
        final List<ItemStack> itemList = new ArrayList<>();
        if (!placementContext.fancyPlacement())
        {
            itemList.add(BlockUtils.getItemStackFromBlockState(blockState));
            return itemList;
        }
        if (WorldUtil.isNetherType(world) && blockState.getBlock() == Blocks.LAVA)
        {
            return Collections.emptyList();
        }
        else if (blockState.getBlock() == Blocks.WATER)
        {
            return Collections.emptyList();
        }

        if (!blockState.getFluidState().isSource())
        {
            return Collections.emptyList();
        }

        itemList.add(BlockUtils.getItemStackFromBlockState(blockState));
        return itemList;
    }

    @Override
    public IPlacementHandler.ActionProcessingResult handle(
      @NotNull World world,
      @NotNull int[] pos,
      @NotNull BlockState blockState,
      @Nullable NBTTagCompound tileEntityData,
      @NotNull final IPlacementContext placementContext)
    {
        if (!blockState.getFluidState().isSource() && placementContext.fancyPlacement())
        {
            return ActionProcessingResult.PASS;
        }
        world.setBlock(pos, blockState, UPDATE_FLAG);
        world.scheduleTick(pos, blockState.getFluidState().getType(), blockState.getFluidState().getType().getTickDelay(world));
        return ActionProcessingResult.SUCCESS;
    }

    @Override
    public boolean doesWorldStateMatchBlueprintState(
        final BlockState blueprintState,
        final BlockState worldState,
        final Tuple<BlockEntity, NBTTagCompound> tuple,
        @NotNull final IPlacementContext iPlacementContext)
    {
        return blueprintState.equals(worldState);
    }
}




