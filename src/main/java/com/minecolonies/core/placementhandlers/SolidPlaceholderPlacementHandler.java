package com.minecolonies.core.placementhandlers;

import com.ldtteam.structurize.blocks.ModBlocks;
import com.ldtteam.structurize.blocks.schematic.BlockSolidSubstitution;
import com.ldtteam.structurize.placement.IPlacementContext;
import com.ldtteam.structurize.placement.handlers.placement.IPlacementHandler;
import com.ldtteam.structurize.placement.handlers.placement.PlacementHandlers;
import com.ldtteam.structurize.util.BlockUtils;
import com.ldtteam.structurize.util.PlacementSettings;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import com.minecolonies.api.util.Tuple;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.init.Blocks;
// [1.7.10] block.entity removed
// [1.7.10] BlockState -> int metadata
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.ldtteam.structurize.api.util.constant.Constants.UPDATE_FLAG;

public class SolidPlaceholderPlacementHandler implements IPlacementHandler
{
    /**
     * Replacement block for solid placeholders
     */
    private BlockState replacement = Blocks.DIRT.defaultBlockState();

    private IPlacementHandler replacementHandler = null;

    /**
     * Sets a different replacement block
     *
     * @param state
     */
    public void setReplacement(final BlockState state)
    {
        replacement = state;
    }

    /**
     * Get the replacement block
     *
     * @return
     */
    public BlockState getReplacement()
    {
        return replacement;
    }

    @Override
    public boolean canHandle(World world, int[] pos, BlockState blockState)
    {
        return blockState.getBlock() instanceof BlockSolidSubstitution;
    }

    private void searchHandler(final World world, final int[] pos)
    {
        if (replacementHandler == null)
        {
            for (final IPlacementHandler handler : PlacementHandlers.handlers)
            {
                if (handler != this && handler.canHandle(world, pos, replacement))
                {
                    replacementHandler = handler;
                    break;
                }
            }
        }
    }

    @Override
    public List<ItemStack> getRequiredItems(
        World world,
        int[] pos,
        BlockState blockState,
        @Nullable NBTTagCompound tileEntityData,
        @NotNull final IPlacementContext placementContext)
    {
        searchHandler(world, pos);
        List<ItemStack> items = new ArrayList<>();

        if (!placementContext.fancyPlacement())
        {
            // for scan tool, show the actual placeholder block
            items.add(new ItemStack(blockState.getBlock()));
        }
        else
        {
            return replacementHandler.getRequiredItems(world, pos, replacement, tileEntityData, placementContext);
        }

        return items;
    }

    @Override
    public ActionProcessingResult handle(
        final World world,
        final int[] pos,
        final BlockState blockState,
        @Nullable final NBTTagCompound tileEntityData,
        @NotNull final IPlacementContext placementContext)
    {
        if (!placementContext.fancyPlacement())
        {
            world.setBlock(pos, ModBlocks.blockSubstitution.get().defaultBlockState(), UPDATE_FLAG);
            return ActionProcessingResult.SUCCESS;
        }

        if (BlockUtils.isAnySolid(world.getBlockState(pos)))
        {
            return ActionProcessingResult.PASS;
        }

        searchHandler(world, pos);
        return replacementHandler.handle(world, pos, replacement, tileEntityData, placementContext);
    }

    @Override
    public boolean doesWorldStateMatchBlueprintState(
        final BlockState worldState,
        final BlockState blueprintState,
        final Tuple<BlockEntity, NBTTagCompound> blockEntityData,
        @NotNull final IPlacementContext placementContext)
    {
        return worldState.equals(blueprintState) || (placementContext.fancyPlacement() && BlockUtils.isGoodFloorBlock(worldState));
    }
}



