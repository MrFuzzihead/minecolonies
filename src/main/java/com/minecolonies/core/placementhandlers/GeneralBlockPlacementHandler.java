package com.minecolonies.core.placementhandlers;

import com.ldtteam.domumornamentum.block.decorative.PillarBlock;
import com.ldtteam.structurize.api.util.ItemStackUtils;
import com.ldtteam.structurize.placement.IPlacementContext;
import com.ldtteam.structurize.placement.handlers.placement.IPlacementHandler;
import com.ldtteam.structurize.util.BlockUtils;
import com.minecolonies.api.compatibility.candb.ChiselAndBitsCheck;
import com.minecolonies.api.util.Log;
import com.minecolonies.api.util.WorldUtil;
import net.minecraft.nbt.NBTTagCompound;
import com.minecolonies.api.util.Tuple;
import net.minecraft.item.ItemStack;
import net.minecraft.block.state.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockWall;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.ldtteam.structurize.placement.handlers.placement.PlacementHandlers.handleTileEntityPlacement;

public class GeneralBlockPlacementHandler implements IPlacementHandler
{
    @Override
    public boolean canHandle(@NotNull final World world, @NotNull final int[] pos, @NotNull final BlockState blockState)
    {
        return true;
    }

    @Override
    public ActionProcessingResult handle(
      @NotNull final World world,
      @NotNull final int[] pos,
      @NotNull final BlockState blockState,
      @Nullable final NBTTagCompound tileEntityData,
      @NotNull final IPlacementContext context)
    {
        BlockState placementState = blockState;
        if (blockState.getBlock() instanceof BlockWall || blockState.getBlock() instanceof BlockFence || blockState.getBlock() instanceof PillarBlock || blockState.getBlock() instanceof BlockPane)
        {
            try
            {
                final BlockState tempState = blockState.getBlock().getStateForPlacement(
                  new BlockPlaceContext(world, null, 0 /* InteractionHand.MAIN_HAND */, null,
                    new BlockHitResult(new Vec3(0, 0, 0), Direction.DOWN, pos, true)));
                if (tempState != null)
                {
                    placementState = tempState;
                }
            }
            catch (final Exception ex)
            {
                // Noop
            }
        }

        if (world.getBlockState(pos).equals(placementState))
        {
            return ActionProcessingResult.PASS;
        }

        if (!WorldUtil.setBlockState(world, pos, placementState, com.ldtteam.structurize.api.util.constant.Constants.UPDATE_FLAG))
        {
            return ActionProcessingResult.PASS;
        }

        if (tileEntityData != null)
        {
            try
            {
                handleTileEntityPlacement(tileEntityData, world, pos, context.getRotationMirror());
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
      @NotNull final World world,
      @NotNull final int[] pos,
      @NotNull final BlockState blockState,
      @Nullable final NBTTagCompound tileEntityData,
      @NotNull final IPlacementContext placementContext)
    {
        final List<ItemStack> itemList = new ArrayList<>();
        if (!ChiselAndBitsCheck.isChiselAndBitsBlock(blockState))
        {
            itemList.add(BlockUtils.getItemStackFromBlockState(blockState));
        }
        if (tileEntityData != null)
        {
            itemList.addAll(ItemStackUtils.getItemStacksOfTileEntity(tileEntityData, blockState));
        }
        itemList.removeIf(ItemStackUtils::isEmpty);

        return itemList;
    }

    @Override
    public boolean doesWorldStateMatchBlueprintState(
        final BlockState blueprintState,
        final BlockState worldState,
        final Tuple<TileEntity, NBTTagCompound> tuple,
        @NotNull final IPlacementContext iPlacementContext)
    {
        if (worldState.equals(blueprintState))
        {
            return true;
        }
        return blueprintState.getBlock() == worldState.getBlock()
            && (blueprintState.getBlock() instanceof BlockWall
            || blueprintState.getBlock() instanceof BlockFence
            || blueprintState.getBlock() instanceof BlockPane
            || blueprintState.getBlock() instanceof BlockFenceGate);
    }
}






