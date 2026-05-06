package com.minecolonies.core.colony.buildings.workerbuildings;
import net.minecraft.util.Direction;
// [1.7.10] removed: import net.minecraft.core.Direction; (use net.minecraft.util.Direction)
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;

import com.google.common.collect.ImmutableList;
import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.constant.NbtTagConstants;
import com.minecolonies.core.colony.buildings.AbstractBuilding;
import com.minecolonies.core.datalistener.model.Disease;
import com.minecolonies.core.datalistener.DiseasesListener;
import com.minecolonies.core.entity.ai.workers.util.Patient;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTBase;
// [1.7.10] tags removed
import com.minecolonies.api.util.Tuple;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.level.block.BedBlock;
// [1.7.10] BlockState -> int metadata
import net.minecraft.world.level.block.state.properties.BedPart;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static com.minecolonies.api.util.constant.NbtTagConstants.*;
import static com.minecolonies.api.util.constant.Suppression.OVERRIDE_EQUALS;

/**
 * Class of the hospital building.
 */
@SuppressWarnings(OVERRIDE_EQUALS)
public class BuildingHospital extends AbstractBuilding
{
    /**
     * The hospital string.
     */
    private static final String HOSPITAL_DESC = "hospital";

    /**
     * Max building World of the hospital.
     */
    private static final int MAX_BUILDING_LEVEL = 5;

    /**
     * Map from beds to patients, 0 is empty.
     */
    @NotNull
    private final Map<int[], Integer> bedMap = new HashMap<>();

    /**
     * Map of patients of this hospital.
     */
    private final Map<Integer, Patient> patients = new HashMap<>();

    /**
     * Instantiates a new hospital building.
     *
     * @param c the colony.
     * @param l the location
     */
    public BuildingHospital(final IColony c, final int[] l)
    {
        super(c, l);
    }

    @NotNull
    @Override
    public String getSchematicName()
    {
        return HOSPITAL_DESC;
    }

    @Override
    public int getMaxBuildingLevel()
    {
        return MAX_BUILDING_LEVEL;
    }

    @Override
    public void deserializeNBT(final NBTTagCompound compound)
    {
        super.deserializeNBT(compound);
        final NBTTagList bedTagList = compound.getTagList(TAG_BEDS, NBTBase.TAG_COMPOUND);
        for (int i = 0; i < bedTagList.size(); ++i)
        {
            final NBTTagCompound bedCompound = bedTagList.getCompoundTagAt(i);
            final int[] bedPos = BlockPosUtil.read(bedCompound, TAG_POS);
            if (!bedMap.containsKey(bedPos))
            {
                bedMap.put(bedPos, bedCompound.getInt(TAG_ID));
            }
        }

        final NBTTagList patientTagList = compound.getTagList(TAG_PATIENTS, NBTBase.TAG_COMPOUND);
        for (int i = 0; i < patientTagList.size(); ++i)
        {
            final NBTTagCompound patientCompound = patientTagList.getCompoundTagAt(i);
            final int patientId = patientCompound.getInt(TAG_ID);
            if (!patients.containsKey(patientId))
            {
                patients.put(patientId, new Patient(patientCompound));
            }
        }
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        final NBTTagCompound compound = super.serializeNBT();
        if (!bedMap.isEmpty())
        {
            @NotNull final NBTTagList bedTagList = new NBTTagList();
            for (@NotNull final Map.Entry<int[], Integer> entry : bedMap.entrySet())
            {
                final NBTTagCompound bedCompound = new NBTTagCompound();
                BlockPosUtil.write(bedCompound, NbtTagConstants.TAG_POS, entry.getKey());
                bedCompound.putInt(TAG_ID, entry.getValue());
                bedTagList.add(bedCompound);
            }
            compound.setTag(TAG_BEDS, bedTagList);
        }

        if (!patients.isEmpty())
        {
            @NotNull final NBTTagList patientTagList = new NBTTagList();
            for (@NotNull final Patient patient : patients.values())
            {
                final NBTTagCompound patientCompound = new NBTTagCompound();
                patient.write(patientCompound);
                patientTagList.add(patientCompound);
            }
            compound.setTag(TAG_PATIENTS, patientTagList);
        }

        return compound;
    }

    @Override
    public void registerBlockPosition(@NotNull final BlockState blockState, @NotNull final int[] pos, @NotNull final World world)
    {
        super.registerBlockPosition(blockState, pos, world);

        int[] registrationPosition = pos;
        if (blockState.getBlock() instanceof BedBlock)
        {
            if (blockState.getValue(BedBlock.PART) == BedPart.FOOT)
            {
                registrationPosition = registrationPosition.relative(blockState.getValue(BedBlock.FACING));
            }

            if (!bedMap.containsKey(registrationPosition))
            {
                bedMap.put(registrationPosition, 0);
            }
        }
    }

    /**
     * Get the list of beds.
     *
     * @return immutable copy
     */
    @NotNull
    public List<int[]> getBedList()
    {
        return ImmutableList.copyOf(bedMap.keySet());
    }

    /**
     * Get the list of patient files.
     *
     * @return immutable copy.
     */
    public List<Patient> getPatients()
    {
        return ImmutableList.copyOf(patients.values());
    }

    /**
     * Remove a patient from the list.
     *
     * @param patient the patient to remove.
     */
    public void removePatientFile(final Patient patient)
    {
        patients.remove(patient.getId());
    }

    @Override
    public Map<Predicate<ItemStack>, Tuple<Integer, Boolean>> getRequiredItemsAndAmount()
    {
        final Map<Predicate<ItemStack>, Tuple<Integer, Boolean>> map = super.getRequiredItemsAndAmount();
        map.put(BuildingHospital::isCureItem, new Tuple<>(10, false));
        return map;
    }

    /**
     * Check if the given itemstack is a cure item.
     *
     * @param stack the stack to test.
     * @return true if so.
     */
    private static boolean isCureItem(final ItemStack stack)
    {
        for (final Disease disease : DiseasesListener.getDiseases())
        {
            for (final ItemStorage cureItem : disease.cureItems())
            {
                if (Disease.isCureItem(stack, cureItem))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Add a new patient to the list.
     *
     * @param citizenId patient to add.
     */
    public void checkOrCreatePatientFile(final int citizenId)
    {
        if (!patients.containsKey(citizenId))
        {
            patients.put(citizenId, new Patient(citizenId));
        }
    }

    /**
     * Register a citizen.
     *
     * @param bedPos    the pos.
     * @param citizenId the citizen id.
     */
    public void registerPatient(final int[] bedPos, final int citizenId)
    {
        bedMap.put(bedPos, citizenId);
        setBedOccupation(bedPos, citizenId != 0);
    }

    /**
     * Helper method to set bed occupation.
     *
     * @param bedPos   the position of the bed.
     * @param occupied if occupied.
     */
    private void setBedOccupation(final int[] bedPos, final boolean occupied)
    {
        final BlockState state = colony.getWorld().getBlockState(bedPos);
        if (state.is(BlockTags.BEDS))
        {
            colony.getWorld().setBlock(bedPos, state.setValue(BedBlock.OCCUPIED, occupied), 0x03);

            final int[] feetPos = bedPos.relative(state.getValue(BedBlock.FACING).getOpposite());
            final BlockState feetState = colony.getWorld().getBlockState(feetPos);

            if (feetState.is(BlockTags.BEDS))
            {
                colony.getWorld().setBlock(feetPos, feetState.setValue(BedBlock.OCCUPIED, occupied), 0x03);
            }
        }
    }

    @Override
    public void onWakeUp()
    {
        for (final Map.Entry<int[], Integer> entry : new ArrayList<>(bedMap.entrySet()))
        {
            final BlockState state = colony.getWorld().getBlockState(entry.getKey());
            if (state.getBlock() instanceof BedBlock)
            {
                if (entry.getValue() == 0 && state.getValue(BedBlock.OCCUPIED))
                {
                    setBedOccupation(entry.getKey(), false);
                }
                else if (entry.getValue() != 0)
                {
                    final ICitizenData citizen = colony.getCitizenManager().getCivilian(entry.getValue());
                    if (citizen != null)
                    {
                        if (state.getValue(BedBlock.OCCUPIED))
                        {
                            if (!citizen.isAsleep() || citizen.getEntity().isEmpty() || citizen.getEntity().get().blockPosition().distSqr(entry.getKey()) > 2.0)
                            {
                                setBedOccupation(entry.getKey(), false);
                                bedMap.put(entry.getKey(), 0);
                            }
                        }
                        else
                        {
                            if (citizen.isAsleep() && citizen.getEntity().isPresent() && citizen.getEntity().get().blockPosition().distSqr(entry.getKey()) < 2.0)
                            {
                                setBedOccupation(entry.getKey(), true);
                            }
                        }
                    }
                    else
                    {
                        bedMap.put(entry.getKey(), 0);
                    }
                }
            }
            else
            {
                bedMap.remove(entry.getKey());
            }
        }
    }

    @Override
    public boolean canEat(final ItemStack stack)
    {
        if (isCureItem(stack))
        {
            return false;
        }

        return super.canEat(stack);
    }
}





