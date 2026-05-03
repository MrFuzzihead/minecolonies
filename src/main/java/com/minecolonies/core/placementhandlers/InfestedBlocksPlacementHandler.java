package com.minecolonies.core.placementhandlers;

import com.ldtteam.structurize.placement.IPlacementContext;
import com.ldtteam.structurize.placement.handlers.placement.IPlacementHandler;
import com.ldtteam.structurize.util.BlockUtils;
import com.ldtteam.structurize.util.PlacementSettings;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import com.minecolonies.api.util.Tuple;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.level.block.InfestedBlock;
// [1.7.10] block.entity removed
// [1.7.10] BlockState -> int metadata
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.ldtteam.structurize.api.util.constant.Constants.UPDATE_FLAG;

/**
 * Placement handler for replacing infested blocks with their non-infested variants.
 */
public class InfestedBlocksPlacementHandler implements IPlacementHandler
{
    @Override
    public boolean canHandle(final World world, final int[] pos, final BlockState blockState)
    {
        return blockState.getBlock() instanceof InfestedBlock;
    }

    @Override
    public ActionProcessingResult handle(
      final World world,
      final int[] pos,
      final BlockState blockState,
      @Nullable final NBTTagCompound tileEntityData,
      @NotNull final IPlacementContext placementContext)
    {
        final BlockState expectedBlockState = getExpectedBlockState(blockState, !placementContext.fancyPlacement());
        if (expectedBlockState == null)
        {
            return ActionProcessingResult.PASS;
        }

        if (world.getBlockState(pos).equals(expectedBlockState))
        {
            return ActionProcessingResult.PASS;
        }

        if (!world.setBlock(pos, expectedBlockState, UPDATE_FLAG))
        {
            return ActionProcessingResult.DENY;
        }

        return ActionProcessingResult.SUCCESS;
    }

    /**
     * Generates the correct block state for the placement.
     *
     * @param blockState the input block state.
     * @param complete   place it complete (with or without substitution blocks etc.).
     * @return the new block state.
     */
    @Nullable
    private static BlockState getExpectedBlockState(final BlockState blockState, final boolean complete)
    {
        if (blockState.getBlock() instanceof InfestedBlock infestedBlock)
        {
            if (complete)
            {
                return blockState;
            }
            else
            {
                return infestedBlock.hostStateByInfested(blockState);
            }
        }

        return null;
    }

    @Override
    public List<ItemStack> getRequiredItems(final World world,
        final int[] pos,
        final BlockState blockState,
        @Nullable final NBTTagCompound tileEntityData,
        @NotNull final IPlacementContext placementContext)
    {
        final BlockState expectedBlockState = getExpectedBlockState(blockState, !placementContext.fancyPlacement());
        return expectedBlockState != null ? List.of(BlockUtils.getItemStackFromBlockState(expectedBlockState)) : List.of();
    }

    @Override
    public boolean doesWorldStateMatchBlueprintState(
        final BlockState worldState,
        final BlockState blueprintState,
        final Tuple<BlockEntity, NBTTagCompound> blockEntityData,
        @NotNull final IPlacementContext structureHandler)
    {
        return worldState.equals(blueprintState);
    }
}




