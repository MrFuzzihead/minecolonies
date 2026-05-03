package com.minecolonies.core.colony.buildings.workerbuildings.plantation.modules.generic;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;

import com.minecolonies.api.colony.buildingextensions.IBuildingExtension;
import com.minecolonies.api.util.constant.CitizenConstants;
import com.minecolonies.core.colony.buildings.workerbuildings.plantation.AbstractPlantationModule;
import com.minecolonies.core.util.CollectorUtils;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
// [1.7.10] BlockState -> int metadata
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Plantation module for plants that grow attached to any horizontal side of a tree log.
 * Similar to plants like cocoa beans.
 * <br/>
 * Requirements:
 * <ol>
 *     <li>Must grow on the side of a vertically standing log.</li>
 *     <li>Must be harvested by chopping and re-planting the plant of the tree.</li>
 *     <li>Every harvestable must be within {@link CitizenConstants#DEFAULT_RANGE_FOR_DELAY} blocks range of any available walking position, if not the entity will not be able path to the position.</li>
 * </ol>
 */
public abstract class TreeSidePlantModule extends AbstractPlantationModule
{
    /**
     * Default constructor.
     *
     * @param field    the field instance this module is working on.
     * @param fieldTag the NBTBase of the field anchor block.
     * @param workTag  the NBTBase of the working positions.
     * @param item     the item which is harvested.
     */
    protected TreeSidePlantModule(
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
    public @Nullable int[] getNextWorkingPosition(final World world)
    {
        for (int[] position : getWorkingPositions())
        {
            if (decideWorkAction(world, position) != ActionToPerform.NONE)
            {
                return position;
            }
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

    @Override
    public List<int[]> getValidWorkingPositions(final @NotNull World world, final List<int[]> workingPositions)
    {
        Set<int[]> treePositions = new HashSet<>();
        for (int[] position : workingPositions)
        {
            for (int[] adjacentPosition : List.of(position.north(), position.south(), position.west(), position.east()))
            {
                if (world.getBlockState(adjacentPosition).isAir())
                {
                    treePositions.add(adjacentPosition);
                }
            }
        }
        return super.getValidWorkingPositions(world, treePositions.stream().collect(CollectorUtils.toShuffledList()));
    }

    @Override
    public int[] getPositionToWalkTo(final World world, final int[] workingPosition)
    {
        return Stream.of(workingPosition.north(), workingPosition.south(), workingPosition.west(), workingPosition.east())
                 .filter(pos -> world.getBlockState(pos).isAir())
                 .findFirst()
                 .orElse(workingPosition);
    }
}


