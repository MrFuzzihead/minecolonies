package com.minecolonies.core.colony.jobs;

import com.minecolonies.core.entity.citizen.EntityCitizen;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.util.ResourceLocation;
import com.minecolonies.api.client.render.modeltype.ModModelTypes;
import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.core.entity.ai.workers.crafting.EntityAIWorkBaker;
// [1.7.10] sounds removed
import org.jetbrains.annotations.NotNull;

/**
 * The fisherman's job class. implements some useful things for him.
 */
public class JobBaker extends AbstractJobCrafter<EntityAIWorkBaker, JobBaker>
{
    /**
     * Initializes the job class.
     *
     * @param entity The entity which will use this job class.
     */
    public JobBaker(final ICitizenData entity)
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
        return ModModelTypes.BAKER_ID;
    }

    /**
     * Generate your AI class to register.
     *
     * @return your personal AI instance.
     */
    @NotNull
    @Override
    public EntityAIWorkBaker generateAI()
    {
        return new EntityAIWorkBaker(this);
    }

    @Override
    public void playSound(final int[] blockPos, final EntityCitizen worker)
    {
        worker.queueSound(SoundEvents.FIRECHARGE_USE, blockPos, 10, 0, 0.5f, 0.1f);
    }
}






