package com.minecolonies.core.placementhandlers;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tileentity.BlockEntity; // [1.7.10] alias -> TileEntity

import com.ldtteam.structurize.placement.IPlacementContext;
import com.ldtteam.structurize.placement.handlers.placement.IPlacementHandler;
import com.ldtteam.structurize.util.BlockUtils;
import com.minecolonies.api.util.Log;
import com.minecolonies.api.util.WorldUtil;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import com.minecolonies.api.util.Tuple;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.level.block.StructureBlock;
import net.minecraft.world.level.block.StructureVoidBlock;
// [1.7.10] block.entity removed
// [1.7.10] BlockState -> int metadata
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static com.ldtteam.structurize.placement.handlers.placement.PlacementHandlers.handleTileEntityPlacement;

/**
 * Handler for some specific blocks that we want only to paste/cost in the complete mode.
 */
public class BuilderIgnorePlacementHandler implements IPlacementHandler
{
    @Override
    public boolean canHandle(@NotNull final World world, @NotNull final int[] pos, @NotNull final BlockState blockState)
    {
        return blockState.getBlock() instanceof StructureBlock || blockState.getBlock() instanceof StructureVoidBlock;
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
            WorldUtil.setBlockState(world, pos, blockState, com.ldtteam.structurize.api.util.constant.Constants.UPDATE_FLAG);
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
            return Collections.singletonList(new ItemStack(blockState.getBlock()));
        }
        return Collections.emptyList();
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




