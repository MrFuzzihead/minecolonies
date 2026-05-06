package com.minecolonies.core.colony.buildings.workerbuildings.plantation.modules.specific;
import net.minecraft.util.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
// [1.7.10] removed: import net.minecraft.core.Direction; (use net.minecraft.util.Direction)
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.level.chunk.LevelChunk;

import com.minecolonies.api.colony.buildingextensions.IBuildingExtension;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import com.minecolonies.api.equipment.ModEquipmentTypes;
import com.minecolonies.api.equipment.registry.EquipmentTypeEntry;
import com.minecolonies.core.colony.buildings.workerbuildings.plantation.modules.generic.BoneMealedPlantModule;
// [1.7.10] int[] -> int x,y,z
// [1.7.10] Direction -> net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.init.Blocks;
// [1.7.10] BlockState -> int metadata
// [1.7.10] World.material removed
// [1.7.10] checkerframework not available â€” using @NotNull instead
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.minecolonies.api.research.util.ResearchConstants.PLANTATION_SEA;

/**
 * Planter module for growing {@link Items#SEAGRASS}.
 * <br/>
 * Requirements:
 * <ol>
 *     <li>All requirements from {@link BoneMealedPlantModule}</li>
 *     <li>All working positions must have water directly overhead of them, else the seagrass won't be able to grow.</li>
 * </ol>
 */
public class SeagrassPlantModule extends BoneMealedPlantModule
{
    /**
     * Default constructor.
     *
     * @param field    the field instance this module is working on.
     * @param fieldTag the NBTBase of the field anchor block.
     * @param workTag  the NBTBase of the working positions.
     * @param item     the item which is harvested.
     */
    public SeagrassPlantModule(final IBuildingExtension field, final String fieldTag, final String workTag, final Item item)
    {
        super(field, fieldTag, workTag, item);
    }

    @Override
    public ResourceLocation getRequiredResearchEffect()
    {
        return PLANTATION_SEA;
    }

    @Override
    public void applyBonemeal(final AbstractEntityCitizen worker, final int[] workPosition, final ItemStack stackInSlot, final Player fakePlayer)
    {
        BoneMealItem.growWaterPlant(stackInSlot, worker.World(), workPosition.above(), Direction.UP);
        BoneMealItem.addGrowthParticles(worker.World(), workPosition.above(), 1);
    }

    @Override
    public EquipmentTypeEntry getRequiredTool()
    {
        return ModEquipmentTypes.none.get();
    }

    @Override
    protected boolean isValidHarvestBlock(final BlockState blockState)
    {
        return blockState.getFluidState().is(Fluids.WATER) && (blockState.getBlock() == Blocks.SEAGRASS || blockState.getBlock() == Blocks.TALL_SEAGRASS);
    }

    @Override
    protected boolean isValidBonemealLocation(final BlockState blockState)
    {
        return blockState.is(Blocks.WATER);
    }

    @Override
    public @NotNull List<Item> getValidBonemeal()
    {
        // Only base minecraft bonemeal has water growing capabilities.
        // Compost (by design) should not inherit this functionality.
        return List.of(Items.BONE_MEAL);
    }
}



