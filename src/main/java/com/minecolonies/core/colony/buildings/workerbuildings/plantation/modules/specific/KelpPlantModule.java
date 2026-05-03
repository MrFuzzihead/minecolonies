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
import com.minecolonies.core.colony.buildings.workerbuildings.plantation.modules.generic.UpwardsGrowingPlantModule;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.World;
import net.minecraft.init.Blocks;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
// [1.7.10] BlockState -> int metadata
import org.jetbrains.annotations.NotNull;

import static com.minecolonies.api.research.util.ResearchConstants.PLANTATION_SEA;

/**
 * Planter module for growing {@link Items#KELP}.
 * <br/>
 * Requirements:
 * <ol>
 *     <li>All requirements from {@link UpwardsGrowingPlantModule}</li>
 *     <li>
 *         There must be an air block directly above the water at least {@link KelpPlantModule#MAX_HEIGHT} + 1 from the working position block.
 *         This is where the AI will attempt to walk to.
 *     </li>
 * </ol>
 */
public class KelpPlantModule extends UpwardsGrowingPlantModule
{
    /**
     * The minimum height kelp can grow to.
     */
    private static final int MIN_HEIGHT = 2;

    /**
     * The maximum height kelp can grow to.
     */
    private static final int MAX_HEIGHT = GrowingPlantHeadBlock.MAX_AGE;

    /**
     * Default constructor.
     *
     * @param field    the field instance this module is working on.
     * @param fieldTag the NBTBase of the field anchor block.
     * @param workTag  the NBTBase of the working positions.
     * @param item     the item which is harvested.
     */
    public KelpPlantModule(final IBuildingExtension field, final String fieldTag, final String workTag, final Item item)
    {
        super(field, fieldTag, workTag, item);
    }

    @Override
    protected boolean isValidPlantingBlock(final BlockState blockState)
    {
        return blockState.getBlock() == Blocks.WATER;
    }

    @Override
    protected boolean isValidHarvestBlock(final BlockState blockState)
    {
        return blockState.getBlock() == Blocks.KELP || blockState.getBlock() == Blocks.KELP_PLANT;
    }

    @Override
    protected int getMinimumPlantLength()
    {
        return MIN_HEIGHT;
    }

    @Override
    protected @NotNull Integer getMaximumPlantLength()
    {
        return MAX_HEIGHT;
    }

    @Override
    public int[] getPositionToWalkTo(final World world, final int[] workingPosition)
    {
        // Attempt to initially find an air block somewhere above the kelp planting position, so that we have a valid position
        // that the AI can actually walk to.
        for (int i = 0; i < getMaximumPlantLength() + 1; i++)
        {
            if (world.getBlockState(workingPosition.above(i)).isAir())
            {
                return workingPosition.above(i);
            }
        }

        return workingPosition;
    }

    @Override
    public ResourceLocation getRequiredResearchEffect()
    {
        return PLANTATION_SEA;
    }

    @Override
    public EquipmentTypeEntry getRequiredTool()
    {
        return ModEquipmentTypes.none.get();
    }
}


