package com.minecolonies.core.placementhandlers;

import com.ldtteam.structurize.placement.IPlacementContext;
import com.ldtteam.structurize.placement.handlers.placement.IPlacementHandler;
import com.ldtteam.structurize.placement.handlers.placement.PlacementHandlers;
import com.ldtteam.structurize.util.BlockUtils;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import com.minecolonies.api.util.Tuple;
import net.minecraft.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.world.level.block.LecternBlock;
// [1.7.10] block.entity removed
// [1.7.10] block.entity removed
// [1.7.10] BlockState -> int metadata
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LecternPlacementHandler implements IPlacementHandler
{
    @Override
    public boolean canHandle(@NotNull final World world,
                             @NotNull final int[] pos,
                             @NotNull final BlockState blockState)
    {
        return blockState.getBlock() instanceof LecternBlock;
    }

    @Override
    public List<ItemStack> getRequiredItems(@NotNull final World world,
                                            @NotNull final int[] pos,
                                            @NotNull final BlockState blockState,
                                            @Nullable final NBTTagCompound tileEntityData,
                                            @NotNull final IPlacementContext placementContext)
    {
        final List<ItemStack> itemList = new ArrayList<>();
        itemList.add(BlockUtils.getItemStackFromBlockState(blockState));

        final LecternBlockEntity lectern = getLectern(pos, blockState, tileEntityData);
        if (lectern != null && lectern.hasBook())
        {
            itemList.add(new ItemStack(Items.BOOK));
        }

        return itemList;
    }

    @Override
    public ActionProcessingResult handle(@NotNull final World world,
                                         @NotNull final int[] pos,
                                         @NotNull final BlockState blockState,
                                         @Nullable NBTTagCompound tileEntityData,
                                         @NotNull final IPlacementContext placementContext)
    {
        if (!world.setBlock(pos, blockState, Block.UPDATE_ALL))
        {
            return ActionProcessingResult.DENY;
        }

        if (tileEntityData != null)
        {
            PlacementHandlers.handleTileEntityPlacement(tileEntityData, world, pos, placementContext.getRotationMirror());
        }

        return ActionProcessingResult.SUCCESS;
    }

    @Nullable
    private static LecternBlockEntity getLectern(@NotNull final int[] pos,
                                                 @NotNull final BlockState blockState,
                                                 @Nullable final NBTTagCompound tileEntityData)
    {
        if (tileEntityData != null)
        {
            final BlockEntity tileEntity = BlockEntity.loadStatic(pos, blockState, tileEntityData);
            if (tileEntity instanceof LecternBlockEntity)
            {
                return (LecternBlockEntity) tileEntity;
            }
        }
        return null;
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




