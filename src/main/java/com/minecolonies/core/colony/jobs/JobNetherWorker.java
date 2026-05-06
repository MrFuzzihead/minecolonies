package com.minecolonies.core.colony.jobs;

import com.minecolonies.api.client.render.modeltype.ModModelTypes;
import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.core.entity.ai.workers.production.EntityAIWorkNether;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
// [1.7.10] net.minecraft.util.DamageSource removed
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import static com.minecolonies.api.research.util.ResearchConstants.FIRE_DAMAGE_PREDICATE;
import static com.minecolonies.api.research.util.ResearchConstants.FIRE_RES;

public class JobNetherWorker extends AbstractJobCrafter<EntityAIWorkNether, JobNetherWorker>
{
    /**
     * Is the worker in the nether?
     */
    private boolean citizenInNether = false;

    /**
     * Queue of items produced from the initial crafting, containing tokens to be processed
     */
    private Queue<ItemStack> craftedResults =new LinkedList<>();

    /**
     * Post processed queue, no longer contains tokens, or items that were unable to be 'mined' due to tool breakage
     */
    private Queue<ItemStack> processedResults = new LinkedList<>();

    /**
     * NBTBase for storage of the citizenInNether value
     */
    private final String TAG_IN_NETHER = "inNether";

    /**
     * NBTBase for storage of the craftedResults queue
     */
    private final String TAG_CRAFTED = "craftedResults";

    /**
     * NBTBase for storage of the processedResults queue
     */
    private final String TAG_PROCESSED = "processedResults";

    public JobNetherWorker(ICitizenData entity)
    {
        super(entity);
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        final NBTTagCompound compound = super.writeToNBT(new NBTTagCompound());

        @NotNull final NBTTagList craftedList = new NBTTagList();
        craftedResults.forEach(item -> {
            @NotNull final NBTTagCompound itemCompound = item.writeToNBT(new NBTTagCompound());
            craftedList.add(itemCompound);
        });
        compound.setTag(TAG_CRAFTED, craftedList);

        @NotNull final NBTTagList processedList = new NBTTagList();
        processedResults.forEach(item -> {
            @NotNull final NBTTagCompound itemCompound = item.writeToNBT(new NBTTagCompound());
            processedList.add(itemCompound);
        });
        compound.setTag(TAG_PROCESSED, processedList);

        compound.setBoolean(TAG_IN_NETHER, citizenInNether);
        return compound;
    }

    @Override
    public void deserializeNBT(final NBTTagCompound compound)
    {
        super.deserializeNBT(compound);

        final NBTTagList craftedList = compound.getTagList(TAG_CRAFTED, NBTTagCompound.TAG_COMPOUND);
        for (int i = 0; i < craftedList.size(); ++i)
        {
            final NBTTagCompound itemCompound = craftedList.getCompoundTagAt(i);
            craftedResults.add(ItemStack.loadItemStackFromNBT(itemCompound));
        }

        final NBTTagList processedList = compound.getTagList(TAG_PROCESSED, NBTTagCompound.TAG_COMPOUND);
        for (int i = 0; i < processedList.size(); ++i)
        {
            final NBTTagCompound itemCompound = processedList.getCompoundTagAt(i);
            processedResults.add(ItemStack.loadItemStackFromNBT(itemCompound));
        }


        if (compound.hasKey(TAG_IN_NETHER))
        {
            citizenInNether = compound.getBoolean(TAG_IN_NETHER);
        }
    }

    @Override
    public EntityAIWorkNether generateAI()
    {
        return new EntityAIWorkNether(this);
    }

    @NotNull
    @Override
    public ResourceLocation getModel()
    {
        return ModModelTypes.NETHERWORKER_ID;
    }

    @Override
    public double getDiseaseModifier()
    {
        if(this.getCitizen().getEntity().isPresent() && this.getCitizen().getEntity().get().isInvisible())
        {
            return 0;
        }
        return super.getDiseaseModifier();
    }

    @Override
    public int getIdleSeverity(boolean isDemand)
    {
        if(isDemand)
        {
            return super.getIdleSeverity(isDemand);
        }
        else
        {
            // Shorten the time for asking for materials. 
            return 4;
        }
    }

    /**
     * Mark the worker as in the nether or not.
     * @param away true if in the nether
     */
    public void setInNether(boolean away)
    {
        citizenInNether = away;
    }

    /**
     * Check if the citizen is in the nether currently
     */
    public boolean isInNether()
    {
        return citizenInNether;
    }

    /**
     * Get the queue of CraftedResults
     * This queue is not immutable and OK to modify
     */
    public Queue<ItemStack> getCraftedResults()
    {
        return craftedResults;
    }

    /**
     * Add a list of items to the crafted results list
     * @param newResults items to add
     * @return true if success
     */
    public boolean addCraftedResultsList(Collection<ItemStack> newResults)
    {
        return craftedResults.addAll(newResults);
    }

    /**
     * Get the queue of ProcessedResults
     * This queue is not immutable and OK to modify
     */
    public Queue<ItemStack> getProcessedResults()
    {
        return processedResults;
    }

    /**
     * Add a list of items to the processed results list
     * @param newResults items to add
     * @return true if success
     */
    public boolean addProcessedResultsList(Collection<ItemStack> newResults)
    {
        return processedResults.addAll(newResults);
    }

    @Override
    public boolean ignoresDamage(@NotNull final net.minecraft.util.DamageSource source)
    {
        if (net.minecraft.util.DamageSource.typeHolder().is(FIRE_DAMAGE_PREDICATE))
        {
            return getColony().getResearchManager().getResearchEffects().getEffectStrength(FIRE_RES) > 0;
        }

        return super.ignoresDamage(net.minecraft.util.DamageSource);
    }
}





