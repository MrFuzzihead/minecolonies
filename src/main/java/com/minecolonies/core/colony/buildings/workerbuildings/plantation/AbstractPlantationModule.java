package com.minecolonies.core.colony.buildings.workerbuildings.plantation;
import net.minecraft.util.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
// [1.7.10] removed: import net.minecraft.core.Direction; (use net.minecraft.util.Direction)
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.level.chunk.LevelChunk;

import com.minecolonies.api.colony.buildingextensions.IBuildingExtension;
import com.minecolonies.api.colony.buildingextensions.plantation.IPlantationModule;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import com.minecolonies.core.colony.buildingextensions.PlantationField;
import com.minecolonies.core.colony.buildingextensions.modules.AbstractBuildingExtensionModule;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
// [1.7.10] BlockState -> int metadata
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for planter modules that determines how the AI should work specific fields.
 */
public abstract class AbstractPlantationModule extends AbstractBuildingExtensionModule implements IPlantationModule
{
    /**
     * The default maximum amount of plants the field can have.
     */
    protected static final int DEFAULT_MAX_PLANTS = 20;

    /**
     * The NBTBase that the field anchor block contains in order to select which of these modules to use.
     */
    private final String fieldTag;

    /**
     * The NBTBase that the individual working positions must contain.
     */
    private final String workTag;

    /**
     * The block which is harvested in this module.
     */
    private final Item item;

    /**
     * Default constructor.
     *
     * @param field    the field instance this module is working on.
     * @param fieldTag the NBTBase of the field anchor block.
     * @param workTag  the NBTBase of the working positions.
     * @param item     the item which is harvested.
     */
    protected AbstractPlantationModule(
      final IBuildingExtension field, final String fieldTag, final String workTag, final Item item)
    {
        super(field);
        this.fieldTag = fieldTag;
        this.workTag = workTag;
        this.item = item;
    }

    @Override
    public final String getFieldTag()
    {
        return fieldTag;
    }

    @Override
    public final String getWorkTag()
    {
        return workTag;
    }

    @Override
    public final Item getItem()
    {
        return item;
    }

    @Override
    public int getPlantsToRequest()
    {
        return (int) Math.ceil(new ItemStack(item).getMaxStackSize() / 4d);
    }

    @Override
    public ResourceLocation getRequiredResearchEffect()
    {
        return null;
    }

    @Override
    public List<int[]> getValidWorkingPositions(final @NotNull World world, final List<int[]> workingPositions)
    {
        List<int[]> result = new ArrayList<>();
        int maxWorkingPositions = getMaxWorkingPositions();
        for (int i = 0; i < maxWorkingPositions; i++)
        {
            if (workingPositions.size() == i)
            {
                break;
            }
            result.add(workingPositions.get(i));
        }
        return result;
    }

    /**
     * Get the maximum amount of working positions this field is allowed to handle.
     * Defaults to {@link AbstractPlantationModule#DEFAULT_MAX_PLANTS}.
     *
     * @return the maximum amount of plants.
     */
    protected int getMaxWorkingPositions()
    {
        return DEFAULT_MAX_PLANTS;
    }

    @Override
    public List<Item> getValidBonemeal()
    {
        return List.of();
    }

    @Override
    public int[] getPositionToWalkTo(final World world, final int[] workingPosition)
    {
        return workingPosition;
    }

    @Override
    public BlockState getPlantingBlockState(final World world, final int[] workPosition, final BlockState blockState)
    {
        return blockState;
    }

    @Override
    public void applyBonemeal(AbstractEntityCitizen worker, int[] workPosition, ItemStack stackInSlot, Player fakePlayer)
    {
        BoneMealItem.applyBonemeal(stackInSlot, worker.World(), workPosition, fakePlayer);
        BoneMealItem.addGrowthParticles(worker.World(), workPosition, 1);
    }

    /**
     * Get the working positions on the field.
     *
     * @return a list of working positions.
     */
    protected final List<int[]> getWorkingPositions()
    {
        if (extension instanceof PlantationField plantationField)
        {
            return plantationField.getWorkingPositions();
        }
        return new ArrayList<>();
    }

    @Override
    public int hashCode()
    {
        return fieldTag.hashCode();
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        final AbstractPlantationModule that = (AbstractPlantationModule) o;

        return fieldTag.equals(that.fieldTag);
    }
}


