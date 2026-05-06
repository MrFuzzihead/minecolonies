package com.minecolonies.core.colony.jobs;

import com.minecolonies.api.client.render.modeltype.ModModelTypes;
import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.StatsUtil;
import com.minecolonies.api.util.Tuple;
import com.minecolonies.core.entity.ai.workers.production.agriculture.EntityAIWorkFisherman;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.ResourceLocation;
// [1.7.10] net.minecraft.util.DamageSource removed
import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.minecolonies.api.util.constant.NbtTagConstants.*;
import static com.minecolonies.api.util.constant.StatisticsConstants.ITEM_OBTAINED;

/**
 * The fisherman's job class. implements some useful things for him.
 */
public class JobFisherman extends AbstractJob<EntityAIWorkFisherman, JobFisherman>
{
    /**
     * The water the fisherman is currently fishing at Contains the location of the water so that the fisherman can path to the fishing spot.
     */
    private Tuple<int[], int[]> water;

    /**
     * Contains all possible fishing spots. This list is filled during the execution of the fisherman. The fisherman will go from spot to spot and always store the location in this
     * list. After the fisherman has visited an fixed amount of ponds the fisherman will choose a random pond from this list as the next fishing spot. The first position is the
     * water to fish in, the second is the land to stand on.
     */
    @NotNull
    private ArrayList<Tuple<int[], int[]>> ponds = new ArrayList<>();

    /**
     * Initializes the job class.
     *
     * @param entity The entity which will use this job class.
     */
    public JobFisherman(final ICitizenData entity)
    {
        super(entity);
    }

    /**
     * Get the RenderBipedCitizen.Model to use when the Citizen performs this job role.
     *
     * @return Model of the citizen.
     */
    @NotNull
    @Override
    public ResourceLocation getModel()
    {
        return ModModelTypes.FISHERMAN_ID;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        final NBTTagCompound compound = super.serializeNBT();

        @NotNull final NBTTagCompound waterTag = new NBTTagCompound();
        if (water != null)
        {
            BlockPosUtil.write(waterTag, TAG_WATER_POND, water.getA());
            BlockPosUtil.write(waterTag, TAG_PARENT_POND, water.getB());
        }

        @NotNull final NBTTagList lakes = new NBTTagList();
        for (@NotNull final Tuple<int[], int[]> pond : ponds)
        {
            final NBTTagCompound compoundNBT = new NBTTagCompound();
            BlockPosUtil.write(compoundNBT, TAG_WATER_POND, pond.getA());
            BlockPosUtil.write(compoundNBT, TAG_PARENT_POND, pond.getB());
            lakes.add(compoundNBT);
        }
        compound.setTag(TAG_PONDS, lakes);

        return compound;
    }

    @Override
    public void deserializeNBT(final NBTTagCompound compound)
    {
        super.deserializeNBT(compound);

        if (compound.hasKey(TAG_WATER_POND))
        {
            water = new Tuple<>(BlockPosUtil.read(compound, TAG_WATER_POND), BlockPosUtil.read(compound, TAG_PARENT_POND));
        }

        ponds = new ArrayList<>();

        if (compound.hasKey(TAG_PONDS))
        {
            final NBTTagList listOfPonds = compound.getTagList(TAG_PONDS, NBTBase.TAG_COMPOUND);
            for (int i = 0; i < listOfPonds.size(); i++)
            {
                ponds.add(new Tuple<>(BlockPosUtil.read(listOfPonds.getCompoundTagAt(i), TAG_WATER_POND), BlockPosUtil.read(listOfPonds.getCompoundTagAt(i), TAG_PARENT_POND)));
            }
        }
    }

    /**
     * Generate your AI class to register.
     *
     * @return your personal AI instance.
     */
    @NotNull
    @Override
    public EntityAIWorkFisherman generateAI()
    {
        return new EntityAIWorkFisherman(this);
    }

    @Override
    public void triggerDeathAchievement(final net.minecraft.util.DamageSource source, final AbstractEntityCitizen citizen)
    {
        super.triggerDeathAchievement(source, citizen);
    }

    /**
     * Getter for current water.
     *
     * @return Location of the current water block.
     */
    public Tuple<int[], int[]> getWater()
    {
        return water;
    }

    /**
     * Setter for current water.
     *
     * @param water New location for the current water block.
     */
    public void setWater(final Tuple<int[], int[]> water)
    {
        this.water = water;
    }

    /**
     * Returns a safe copy of all current ponds.
     *
     * @return a list of coordinates.
     */
    @NotNull
    public List<Tuple<int[], int[]>> getPonds()
    {
        return new ArrayList<>(ponds);
    }

    /**
     * Add one pond to our list of ponds.
     *
     * @param pond   the pond to add.
     * @param parent the parent position.
     */
    public void addToPonds(final int[] pond, final int[] parent)
    {
        this.ponds.add(new Tuple<>(pond, parent));
    }

    /**
     * remove one pond from the ponds list.
     *
     * @param pond the coordinate pair matching one pond.
     */
    public void removeFromPonds(final Tuple<int[], int[]> pond)
    {
        this.ponds.remove(pond);
    }

    /**
     * Records in hut statistics what the Fisherperson has collected.
     *
     * @param pickedUpStack The stack that is being picked up.
     * @return true when the stack has been used to resolve a request, false when not.
     */
    @Override
    public boolean onStackPickUp(@NotNull final ItemStack pickedUpStack)
    {
        StatsUtil.trackStatByName(getCitizen().getWorkBuilding(), ITEM_OBTAINED, pickedUpStack.getHoverName(), pickedUpStack.getCount());

        return super.onStackPickUp(pickedUpStack);
    }
}






