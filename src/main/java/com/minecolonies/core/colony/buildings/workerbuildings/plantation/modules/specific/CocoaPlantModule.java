package com.minecolonies.core.colony.buildings.workerbuildings.plantation.modules.specific;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;

import com.minecolonies.api.colony.buildingextensions.IBuildingExtension;
import com.minecolonies.api.equipment.ModEquipmentTypes;
import com.minecolonies.api.equipment.registry.EquipmentTypeEntry;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.core.colony.buildings.workerbuildings.plantation.modules.generic.TreeSidePlantModule;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
// [1.7.10] BlockState -> int metadata

import java.util.stream.Stream;

import static com.minecolonies.api.research.util.ResearchConstants.PLANTATION_JUNGLE;

/**
 * Planter module for growing {@link Items#COCOA_BEANS}.
 * <br/>
 * Requirements:
 * <ol>
 *     <li>All requirements from {@link TreeSidePlantModule}</li>
 * </ol>
 */
public class CocoaPlantModule extends TreeSidePlantModule
{
    /**
     * Default constructor.
     *
     * @param field    the field instance this module is working on.
     * @param fieldTag the NBTBase of the field anchor block.
     * @param workTag  the NBTBase of the working positions.
     * @param item     the item which is harvested.
     */
    public CocoaPlantModule(final IBuildingExtension field, final String fieldTag, final String workTag, final Item item)
    {
        super(field, fieldTag, workTag, item);
    }

    @Override
    public ResourceLocation getRequiredResearchEffect()
    {
        return PLANTATION_JUNGLE;
    }

    @Override
    public BlockState getPlantingBlockState(final World world, final int[] workPosition, final BlockState blockState)
    {
        return Stream.of(workPosition.north(), workPosition.south(), workPosition.west(), workPosition.east())
                 .filter(position -> world.getBlockState(position).getBlock() == Blocks.JUNGLE_LOG)
                 .map(position -> BlockPosUtil.directionFromDelta(position.subtract(workPosition).getX(),
                   position.subtract(workPosition).getY(),
                   position.subtract(workPosition).getZ()))
                 .map(direction -> blockState.setValue(HorizontalDirectionalBlock.FACING, direction))
                 .findFirst()
                 .orElse(blockState);
    }

    @Override
    public EquipmentTypeEntry getRequiredTool()
    {
        return ModEquipmentTypes.axe.get();
    }

    @Override
    protected boolean isValidClearingBlock(final BlockState blockState)
    {
        return blockState.getBlock() != Blocks.COCOA;
    }

    @Override
    protected boolean isValidHarvestBlock(final BlockState blockState)
    {
        Block block = blockState.getBlock();
        if (block instanceof CocoaBlock cocoa)
        {
            return !cocoa.isRandomlyTicking(blockState);
        }
        return false;
    }
}


