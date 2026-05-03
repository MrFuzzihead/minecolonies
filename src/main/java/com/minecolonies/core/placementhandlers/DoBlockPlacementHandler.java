package com.minecolonies.core.placementhandlers;

// [1.7.10] DomumOrnamentum not available in 1.7.10 — handler always returns PASS.
import com.ldtteam.structurize.api.util.ItemStackUtils;
import com.ldtteam.structurize.placement.IPlacementContext;
import com.ldtteam.structurize.placement.handlers.placement.IPlacementHandler;
import com.minecolonies.api.blocks.ModBlocks;
import com.minecolonies.api.util.Log;
import com.minecolonies.api.util.Tuple;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * Placement handler for DomumOrnamentum blocks.
 * [1.7.10] DomumOrnamentum is not available — handler always returns PASS.
 */
public class DoBlockPlacementHandler implements IPlacementHandler
{
    @Override
    public boolean canHandle(@NotNull final World world, @NotNull final int[] pos, @NotNull final BlockState blockState)
    {
        // [1.7.10] IMateriallyTexturedBlock not available — never handle
        return false;
    }

    @Override
    public ActionProcessingResult handle(
      @NotNull final World world,
      @NotNull final int[] pos,
      @NotNull final BlockState blockState,
      @Nullable final NBTTagCompound tileEntityData,
      @NotNull final IPlacementContext placementContext)
    {
        return ActionProcessingResult.PASS;
    }

    @Override
    public List<ItemStack> getRequiredItems(
      @NotNull final World world,
      @NotNull final int[] pos,
      @NotNull final BlockState blockState,
      @Nullable final NBTTagCompound tileEntityData,
      @NotNull final IPlacementContext placementContext)
    {
        return Collections.emptyList();
    }

    @Override
    public boolean doesWorldStateMatchBlueprintState(
      final BlockState worldState,
      final BlockState blueprintState,
      final Tuple<Object, NBTTagCompound> blockEntityData,
      final @NotNull IPlacementContext structureHandler)
    {
        return false;
    }

    /**
     * [1.7.10] Stub: returns the appropriate DO item for the given blockstate.
     * DomumOrnamentum is not available in 1.7.10 — always returns the original stack.
     *
     * @param stack           original stack
     * @param state           block state
     * @param fancyPlacement  whether fancy placement is enabled
     * @return the original stack
     */
    public static ItemStack getCorrectDOItem(final ItemStack stack, final BlockState state, final boolean fancyPlacement)
    {
        return stack;
    }
}
