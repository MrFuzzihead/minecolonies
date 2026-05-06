package com.minecolonies.core.colony.buildings.workerbuildings.plantation.modules.generic;
import net.minecraft.util.Direction;
// [1.7.10] removed: import net.minecraft.core.Direction; (use net.minecraft.util.Direction)
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;

import com.minecolonies.api.colony.buildingextensions.IBuildingExtension;
import com.minecolonies.core.colony.buildings.workerbuildings.plantation.AbstractPlantationModule;
import com.minecolonies.core.util.CollectorUtils;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.util.MathHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
// [1.7.10] BlockState -> int metadata
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Plantation module for plants that should always keep an X amount of plants on the ground in order to keep spreading, similar to mushrooms.
 * <br/>
 * Requirements:
 * <ol>
 *     <li>These plants generally have a random growth logic, mushrooms can spread in any direction, not even attached to next to the other mushroom. Vines can spread into any direction, etc.</li>
 *     <li>All the positions you expect the plants to appear have to be tagged.</li>
 * </ol>
 */
public abstract class PercentageHarvestPlantModule extends AbstractPlantationModule
{
    /**
     * Default constructor.
     *
     * @param field    the field instance this module is working on.
     * @param fieldTag the NBTBase of the field anchor block.
     * @param workTag  the NBTBase of the working positions.
     * @param item     the item which is harvested.
     */
    protected PercentageHarvestPlantModule(
      final IBuildingExtension field,
      final String fieldTag,
      final String workTag,
      final Item item)
    {
        super(field, fieldTag, workTag, item);
    }

    @Override
    public PlantationModuleResult.Builder decideFieldWork(final World world, final @NotNull int[] workingPosition)
    {
        ActionToPerform action = decideWorkAction(world, workingPosition);
        return switch (action)
        {
            case HARVEST -> new PlantationModuleResult.Builder()
                              .harvest(workingPosition)
                              .pickNewPosition();
            case PLANT -> new PlantationModuleResult.Builder()
                            .plant(workingPosition)
                            .pickNewPosition();
            case CLEAR -> new PlantationModuleResult.Builder()
                            .clear(workingPosition)
                            .pickNewPosition();
            default -> PlantationModuleResult.NONE;
        };
    }

    /**
     * Responsible for deciding what action the AI is going to perform on a specific field position
     * depending on the state of the working position.
     *
     * @param world            the world reference that can be used for block state lookups.
     * @param plantingPosition the specific position to check for.
     * @return the {@link PlantationModuleResult} that the AI is going to perform.
     */
    private ActionToPerform decideWorkAction(final World world, final int[] plantingPosition)
    {
        BlockState blockState = world.getBlockState(plantingPosition);
        if (isValidPlantingBlock(blockState))
        {
            return ActionToPerform.PLANT;
        }

        if (isValidClearingBlock(blockState))
        {
            return ActionToPerform.CLEAR;
        }

        if (isValidHarvestBlock(blockState))
        {
            return ActionToPerform.HARVEST;
        }

        return ActionToPerform.NONE;
    }

    /**
     * Check if the block is a correct block for planting.
     * Defaults to being any air block.
     *
     * @param blockState the block state.
     * @return whether the block can be planted.
     */
    protected boolean isValidPlantingBlock(BlockState blockState)
    {
        return blockState.isAir();
    }

    /**
     * Check if the block is a correct block for clearing.
     *
     * @param blockState the block state.
     * @return whether the block can be cleared.
     */
    protected boolean isValidClearingBlock(BlockState blockState)
    {
        return !isValidHarvestBlock(blockState);
    }

    /**
     * Check if the block is a correct block for harvesting.
     *
     * @param blockState the block state.
     * @return whether the block can be harvested.
     */
    protected abstract boolean isValidHarvestBlock(BlockState blockState);

    @Override
    public @Nullable int[] getNextWorkingPosition(World world)
    {
        final List<int[]> workingPositions = getWorkingPositions().stream().collect(CollectorUtils.toShuffledList());
        final List<int[]> harvestablePositions = new ArrayList<>();

        final double minimumPlantFraction = Mth.clamp(getMinimumPlantPercentage(), 0, 100) / 100d;
        final int minimumPlantCount = (int) Math.ceil(minimumPlantFraction * workingPositions.size());

        for (int[] position : workingPositions)
        {
            final ActionToPerform action = decideWorkAction(world, position);
            if (action == ActionToPerform.CLEAR)
            {
                return position;
            }
            if (action == ActionToPerform.HARVEST)
            {
                harvestablePositions.add(position);
            }
        }

        if (minimumPlantCount > harvestablePositions.size())
        {
            // We want to prevent putting "harvestable" blocks next to one another as much as possible.
            Set<int[]> excludedPositions = harvestablePositions.stream()
                                                .flatMap(f -> Stream.of(f, f.above(), f.below(), f.north(), f.south(), f.west(), f.east()))
                                                .collect(Collectors.toSet());
            return workingPositions.stream()
                     .filter(f -> !excludedPositions.contains(f))
                     .findFirst()
                     .orElse(null);
        }
        else if (minimumPlantCount < harvestablePositions.size())
        {
            Set<int[]> duplicateLocator = new HashSet<>();
            Set<int[]> harvestablePositionsSet = new HashSet<>(harvestablePositions);
            return harvestablePositions.stream()
                     .flatMap(f -> Stream.of(f, f.above(), f.below(), f.north(), f.south(), f.west(), f.east()))
                     .filter(f -> harvestablePositionsSet.contains(f) && !duplicateLocator.add(f))
                     .findFirst()
                     .orElse(harvestablePositions.get(0));
        }

        return null;
    }

    @Override
    public int getActionLimit()
    {
        return 5;
    }

    @Override
    public List<ItemStack> getRequiredItemsForOperation()
    {
        return List.of(new ItemStack(getItem()));
    }

    /**
     * The percentage of positions the planter should <b>always</b> leave present at the bare minimum.
     * If this is not reached the planter will plant additional plants to reach this minimum.
     *
     * @return a percentage of plants to always leave be.
     */
    protected abstract int getMinimumPlantPercentage();

    @Override
    public int[] getPositionToWalkTo(final World world, final int[] workingPosition)
    {
        return Stream.of(workingPosition.north(), workingPosition.south(), workingPosition.west(), workingPosition.east())
                 .filter(pos -> world.getBlockState(pos).isAir())
                 .findFirst()
                 .orElse(workingPosition);
    }
}



