package com.minecolonies.core.entity.ai.workers.util;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.Direction;

import com.ldtteam.structurize.storage.ServerFutureProcessor;
import com.ldtteam.structurize.storage.StructurePacks;
import com.minecolonies.api.blocks.ModBlocks;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.workorders.IWorkOrder;
import com.minecolonies.api.util.ColonyUtils;
import com.minecolonies.core.blocks.decorative.BlockConstructionTape;
import com.minecolonies.core.colony.Colony;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingTownHall;
// [1.7.10] int[] -> int x,y,z
// [1.7.10] Direction -> net.minecraft.util.EnumFacing
import com.minecolonies.api.util.Tuple;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
// [1.7.10] BlockState -> int metadata
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
// [1.7.10] world.phys removed
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.minecolonies.api.util.constant.Constants.EMPTY_AABB;

/**
 * Helper class to place and remove constructionTapes from the buildings.
 */
public final class ConstructionTapeHelper
{
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty   CORNER = BooleanProperty.create("corner");

    /**
     * Private Constructor to hide implicit one. Intentionally empty.
     */
    private ConstructionTapeHelper() {}

    /**
     * Calculates the borders for the workOrderBuildDecoration and sends it to the placement.
     *
     * @param workOrder the workOrder.
     * @param world     the world.
     */
    public static void placeConstructionTape(@NotNull final IWorkOrder workOrder, @NotNull final World world, final IColony colony)
    {
        final AABB box = workOrder.getBoundingBox();
        if (box != null && box != EMPTY_AABB)
        {
            placeConstructionTape(ColonyUtils.calculateCorners(box), colony);
        }
    }

    /**
     * Calculates the borders for the workOrderBuildDecoration and sends it to the placement.
     *
     * @param building the building.
     */
    public static void placeConstructionTape(@NotNull final IBuilding building)
    {
        ServerFutureProcessor.queueBlueprint(new ServerFutureProcessor.BlueprintProcessingData(StructurePacks.getBlueprintFuture(building.getStructurePack(),
          building.getBlueprintPath()), building.getColony().getWorld(), (blueprint -> {
            final Tuple<int[], int[]> corners =
              ColonyUtils.calculateCorners(building.getPosition(), building.getColony().getWorld(), blueprint, building.getRotation(), building.isMirrored());
            building.setCorners(corners.getA(), corners.getB());
            placeConstructionTape(corners, building.getColony());
        })));
    }

    /**
     * Place construction tape.
     *
     * @param orgCorners the corner positions.
     */
    public static void placeConstructionTape(final Tuple<int[], int[]> orgCorners, final IColony colony)
    {
        if (colony instanceof Colony && !((Colony) colony).getSettings().getSetting(BuildingTownHall.CONSTRUCTION_TAPE).getValue())
        {
            return;
        }

        final World world = colony.getWorld();

        final Tuple<int[], int[]> corners = new Tuple<>(orgCorners.getA().offset(-1, 0, -1), orgCorners.getB().offset(1, 0, 1));
        final BlockState constructionTape = ModBlocks.blockConstructionTape.defaultBlockState();

        final int x = Math.min(corners.getA().getX(), corners.getB().getX());
        final int y = Math.max(corners.getA().getY(), corners.getB().getY());
        final int z = Math.min(corners.getA().getZ(), corners.getB().getZ());
        final int sizeX = Math.abs(corners.getA().getX() - corners.getB().getX());
        final int sizeZ = Math.abs(corners.getA().getZ() - corners.getB().getZ());
        final int sizeY = Math.abs(corners.getA().getY() - corners.getB().getY());
        int[] working;

        for (int[] place = new int[]{x, y, z}; place[0] < x + sizeX || place[2] < z + sizeZ; )
        {

            if (place[0] < x + sizeX)
            {
                working = firstValidPosition(new int[]{place[0], y, z}, world, sizeY);
                if (working != null)
                {
                    world.setBlockAndUpdate(working,
                      BlockConstructionTape.getPlacementState(constructionTape.setValue(CORNER, place[0] == x), world, working, Direction.SOUTH));
                }

                working = firstValidPosition(new int[]{place[0], y, z + sizeZ}, world, sizeY);
                if (working != null)
                {
                    world.setBlockAndUpdate(working,
                      BlockConstructionTape.getPlacementState(constructionTape.setValue(CORNER, place[0] == x), world, working, Direction.NORTH));
                }
            }

            if (place[2] < z + sizeZ)
            {
                working = firstValidPosition(new int[]{x, y, place[2]}, world, sizeY);
                if (working != null)
                {
                    world.setBlockAndUpdate(working, BlockConstructionTape.getPlacementState(constructionTape.setValue(CORNER, place[2] == z), world, working, Direction.EAST));
                }

                working = firstValidPosition(new int[]{x + sizeX, y, place[2]}, world, sizeY);
                if (working != null)
                {
                    world.setBlockAndUpdate(working,
                      BlockConstructionTape.getPlacementState(constructionTape.setValue(CORNER, place[2] == z),
                        world,
                        working,
                        place[2] == z ? Direction.SOUTH : Direction.WEST));
                }
            }

            place = new int[]{place[0]+1, place[1], place[2]+1};
        }

        working = firstValidPosition(new int[]{x + sizeX, y, z + sizeZ}, world, sizeY);
        if (working != null)
        {
            world.setBlockAndUpdate(working, BlockConstructionTape.getPlacementState(constructionTape.setValue(CORNER, true), world, working, Direction.WEST));
        }
    }

    /**
     * Find and return the highest position that is directly above a non-replaceable block.
     *
     * @param target the target position for the block
     * @param world  the world.
     * @return The new block position or null if no valid one is found.
     */
    @Nullable
    public static int[] firstValidPosition(@NotNull final int[] target, @NotNull final World world, final int height)
    {
        for (int i = 0; i <= height + 5; i++)
        {
            final int[] tempTarget = new int[]{target[0], target[1] - i, target[2]};
            final BlockState state = world.getBlockState(tempTarget);
            final BlockState upState = world.getBlockState(new int[]{tempTarget[0], tempTarget[1]+1, tempTarget[2]});

            if (state.isSolid() && !upState.isSolid() && (upState.canBeReplaced() || upState.isAir()))
            {
                return new int[]{tempTarget[0], tempTarget[1]+1, tempTarget[2]};
            }
        }

        return null;
    }

    /**
     * Calculates the borders for the workOrderBuildDecoration and sends it to the removal.
     *
     * @param workOrder the workOrder.
     * @param world     the world.
     */
    public static void removeConstructionTape(@NotNull final IWorkOrder workOrder, @NotNull final World world)
    {
        final AABB box = workOrder.getBoundingBox();
        if (box != null && box != EMPTY_AABB)
        {
            removeConstructionTape(ColonyUtils.calculateCorners(box), world);
        }
    }

    /**
     * Remove construction tape.
     *
     * @param orgCorners the corner positions.
     * @param world      the world.
     */
    public static void removeConstructionTape(final Tuple<int[], int[]> orgCorners, @NotNull final World world)
    {
        final Tuple<int[], int[]> corners = new Tuple<>(orgCorners.getA().offset(-1, 0, -1), orgCorners.getB().offset(1, 0, 1));

        final int x1 = corners.getA().getX();
        final int x3 = corners.getB().getX();
        final int z1 = corners.getA().getZ();
        final int z3 = corners.getB().getZ();

        final int minHeight = Math.min(corners.getB().getY(), corners.getA().getY()) - 5;
        final int maxHeight = Math.max(corners.getB().getY(), corners.getA().getY()) + 1;

        if (x1 < x3)
        {
            for (int i = x1; i <= x3; i++)
            {
                final int[] block1 = new int[]{i, 0, z1};
                final int[] block2 = new int[]{i, 0, z3};
                removeTapeIfNecessary(world, block1, ModBlocks.blockConstructionTape, minHeight, maxHeight);
                removeTapeIfNecessary(world, block2, ModBlocks.blockConstructionTape, minHeight, maxHeight);
            }
        }
        else
        {
            for (int i = x3; i <= x1; i++)
            {
                final int[] block1 = new int[]{i, 0, z1};
                final int[] block2 = new int[]{i, 0, z3};
                removeTapeIfNecessary(world, block1, ModBlocks.blockConstructionTape, minHeight, maxHeight);
                removeTapeIfNecessary(world, block2, ModBlocks.blockConstructionTape, minHeight, maxHeight);
            }
        }
        if (z1 < z3)
        {
            for (int i = z1; i <= z3; i++)
            {
                final int[] block1 = new int[]{x1, 0, i};
                final int[] block2 = new int[]{x3, 0, i};
                removeTapeIfNecessary(world, block1, ModBlocks.blockConstructionTape, minHeight, maxHeight);
                removeTapeIfNecessary(world, block2, ModBlocks.blockConstructionTape, minHeight, maxHeight);
            }
        }
        else
        {
            for (int i = z3; i <= z1; i++)
            {
                final int[] block1 = new int[]{x1, 0, i};
                final int[] block2 = new int[]{x3, 0, i};
                removeTapeIfNecessary(world, block1, ModBlocks.blockConstructionTape, minHeight, maxHeight);
                removeTapeIfNecessary(world, block2, ModBlocks.blockConstructionTape, minHeight, maxHeight);
            }
        }

        final int[] corner1 = new int[]{x1, 0, z1};
        final int[] corner2 = new int[]{x1, 0, z3};
        final int[] corner3 = new int[]{x3, 0, z1};
        final int[] corner4 = new int[]{x3, 0, z3};
        removeTapeIfNecessary(world, corner1, ModBlocks.blockConstructionTape, minHeight, maxHeight);
        removeTapeIfNecessary(world, corner2, ModBlocks.blockConstructionTape, minHeight, maxHeight);
        removeTapeIfNecessary(world, corner3, ModBlocks.blockConstructionTape, minHeight, maxHeight);
        removeTapeIfNecessary(world, corner4, ModBlocks.blockConstructionTape, minHeight, maxHeight);
    }

    /**
     * @param world            the world.
     * @param block            the block.
     * @param tapeOrTapeCorner Is the checked block supposed to be ConstructionTape or ConstructionTapeCorner.
     */
    public static void removeTapeIfNecessary(
      @NotNull final World world,
      @NotNull final int[] block,
      @NotNull final Block tapeOrTapeCorner,
      final int minHeight,
      final int maxHeight)
    {
        for (int y = minHeight; y <= maxHeight; y++)
        {
            final int[] newBlock = new int[]{block[0], y, block[2]};
            if (world.getBlockState(newBlock).getBlock() == tapeOrTapeCorner)
            {
                world.removeBlock(newBlock, false);
                break;
            }
        }
    }
}




