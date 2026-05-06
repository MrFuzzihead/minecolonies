package com.minecolonies.core.colony.buildings.workerbuildings.plantation.modules.specific;
import net.minecraft.util.Direction;
// [1.7.10] removed: import net.minecraft.core.Direction; (use net.minecraft.util.Direction)
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;

import com.minecolonies.api.colony.buildingextensions.IBuildingExtension;
import com.minecolonies.api.equipment.ModEquipmentTypes;
import com.minecolonies.api.equipment.registry.EquipmentTypeEntry;
import com.minecolonies.core.colony.buildings.workerbuildings.plantation.modules.generic.DownwardsGrowingPlantModule;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.init.Blocks;
// [1.7.10] BlockState -> int metadata

import static com.minecolonies.api.research.util.ResearchConstants.PLANTATION_EXOTIC;

/**
 * Planter module for growing {@link Items#GLOW_BERRIES}.
 * <br/>
 * Requirements:
 * <ol>
 *     <li>All requirements from {@link DownwardsGrowingPlantModule}</li>
 * </ol>
 */
public class GlowBerriesPlantModule extends DownwardsGrowingPlantModule
{
    /**
     * Default constructor.
     *
     * @param field    the field instance this module is working on.
     * @param fieldTag the NBTBase of the field anchor block.
     * @param workTag  the NBTBase of the working positions.
     * @param item     the item which is harvested.
     */
    public GlowBerriesPlantModule(final IBuildingExtension field, final String fieldTag, final String workTag, final Item item)
    {
        super(field, fieldTag, workTag, item);
    }

    @Override
    protected boolean isValidHarvestBlock(final BlockState blockState)
    {
        return blockState.getBlock() == Blocks.CAVE_VINES || blockState.getBlock() == Blocks.CAVE_VINES_PLANT;
    }

    @Override
    public ResourceLocation getRequiredResearchEffect()
    {
        return PLANTATION_EXOTIC;
    }

    @Override
    public EquipmentTypeEntry getRequiredTool()
    {
        return ModEquipmentTypes.none.get();
    }
}


