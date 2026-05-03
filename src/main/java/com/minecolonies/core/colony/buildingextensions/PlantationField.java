package com.minecolonies.core.colony.buildingextensions;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;

import com.minecolonies.api.blocks.ModBlocks;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildingextensions.plantation.IPlantationModule;
import com.minecolonies.api.colony.buildingextensions.registry.BuildingExtensionRegistries;
import com.minecolonies.api.colony.buildingextensions.registry.BuildingExtensionRegistries.BuildingExtensionEntry;
import com.minecolonies.api.util.BlockPosUtil;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
// [1.7.10] BlockState -> int metadata
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Field class implementation for the plantation
 */
public class PlantationField extends AbstractBuildingExtensionModule
{
    private static final String TAG_WORKING_POS = "workingPositions";

    /**
     * A list of all found tagged working positions.
     */
    private List<int[]> workingPositions = new ArrayList<>();

    /**
     * Constructor used in NBT deserialization.
     *
     * @param fieldType the type of field.
     * @param position  the position of the field.
     */
    public PlantationField(final @NotNull BuildingExtensionRegistries.BuildingExtensionEntry fieldType, final @NotNull int[] position)
    {
        super(fieldType, position);
    }

    /**
     * Constructor to create new instances
     *
     * @param fieldEntry the type of field we want to produce.
     * @param position   the position it is placed in.
     */
    public static PlantationField create(final BuildingExtensionEntry fieldEntry, final int[] position)
    {
        return (PlantationField) fieldEntry.produceExtension(position);
    }

    @Override
    public boolean isValidPlacement(final IColony colony)
    {
        BlockState blockState = colony.getWorld().getBlockState(getPosition());
        // TODO: future, remove `blockHutPlantation` from valid blocks
        return blockState.is(ModBlocks.blockHutPlantation) || blockState.is(ModBlocks.blockPlantationField);
    }

    /**
     * Get the list of working positions of this field.
     *
     * @return an unmodifiable collection of working positions.
     */
    public List<int[]> getWorkingPositions()
    {
        return workingPositions.stream().toList();
    }

    /**
     * Overwrite the working positions on the field instance.
     *
     * @param workingPositions the new list of working positions.
     */
    public void setWorkingPositions(final List<int[]> workingPositions)
    {
        this.workingPositions = workingPositions;
    }

    /**
     * Get the plantation module on this field.
     *
     * @return the plantation module instance.
     */
    public IPlantationModule getModule()
    {
        return getFirstModuleOccurance(IPlantationModule.class);
    }

    @Override
    public @NotNull NBTTagCompound serializeNBT()
    {
        NBTTagCompound compound = super.serializeNBT();
        BlockPosUtil.writePosListToNBT(compound, TAG_WORKING_POS, workingPositions);
        return compound;
    }

    @Override
    public void deserializeNBT(@NotNull NBTTagCompound compound)
    {
        super.deserializeNBT(compound);
        workingPositions = BlockPosUtil.readPosListFromNBT(compound, TAG_WORKING_POS);
    }

    @Override
    public void serialize(final @NotNull PacketBuffer buf)
    {
        super.serialize(buf);
        buf.writeInt(workingPositions.size());
        for (int[] workingPosition : workingPositions)
        {
            buf.writeBlockPos(workingPosition);
        }
    }

    @Override
    public void deserialize(final @NotNull PacketBuffer buf)
    {
        super.deserialize(buf);
        workingPositions = new ArrayList<>();
        final int workingPositionCount = buf.readInt();
        for (int index = 0; index < workingPositionCount; index++)
        {
            workingPositions.add(buf.readBlockPos());
        }
    }
}



