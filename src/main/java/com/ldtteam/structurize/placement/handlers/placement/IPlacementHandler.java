package com.ldtteam.structurize.placement.handlers.placement;
import net.minecraft.block.state.BlockState;

import com.minecolonies.api.util.Tuple;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.BlockEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * [1.7.10] Source-level override of the (empty) structurize IPlacementHandler interface.
 * Adds ActionProcessingResult nested enum and the conventional method signatures
 * so placement handlers can override them without extra qualifications.
 */
public interface IPlacementHandler
{
    /**
     * Result of a placement handler action.
     */
    enum ActionProcessingResult
    {
        /** The handler processed and succeeded. */
        SUCCESS,
        /** The handler did not handle this block â€” pass to the next handler. */
        PASS,
        /** The handler failed or wants to deny placement. */
        DENY
    }

    /**
     * Whether this handler can handle the given block state.
     */
    boolean canHandle(@NotNull World world, @NotNull int[] pos, @NotNull BlockState blockState);

    /**
     * Execute the placement. Return PASS to let the next handler try.
     */
    ActionProcessingResult handle(
      @NotNull World world,
      @NotNull int[] pos,
      @NotNull BlockState blockState,
      @Nullable NBTTagCompound tileEntityData,
      @NotNull com.ldtteam.structurize.placement.IPlacementContext placementContext);

    /**
     * Returns the items required to place this block.
     */
    List<ItemStack> getRequiredItems(
      @NotNull World world,
      @NotNull int[] pos,
      @NotNull BlockState blockState,
      @Nullable NBTTagCompound tileEntityData,
      @NotNull com.ldtteam.structurize.placement.IPlacementContext placementContext);

    /**
     * Whether the world state matches the blueprint state.
     */
    default boolean doesWorldStateMatchBlueprintState(
      final BlockState blueprintState,
      final BlockState worldState,
      final Tuple<BlockEntity, NBTTagCompound> blockEntityData,
      @NotNull final com.ldtteam.structurize.placement.IPlacementContext structureHandler)
    {
        return blueprintState.equals(worldState);
    }
}

