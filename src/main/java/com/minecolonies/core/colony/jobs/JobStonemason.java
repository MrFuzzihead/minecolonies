package com.minecolonies.core.colony.jobs;

import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.core.entity.ai.workers.crafting.EntityAIWorkStonemason;
import com.minecolonies.core.entity.citizen.EntityCitizen;
// [1.7.10] int[] -> int x,y,z
// [1.7.10] sounds removed
import org.jetbrains.annotations.NotNull;

/**
 * Class of the Stonemason job.
 */
public class JobStonemason extends AbstractJobCrafter<EntityAIWorkStonemason, JobStonemason>
{
    /**
     * Instantiates the job for the Stonemason.
     *
     * @param entity the citizen who becomes a Sawmill
     */
    public JobStonemason(final ICitizenData entity)
    {
        super(entity);
    }

    /**
     * Generate your AI class to register.
     *
     * @return your personal AI instance.
     */
    @NotNull
    @Override
    public EntityAIWorkStonemason generateAI()
    {
        return new EntityAIWorkStonemason(this);
    }

    @Override
    public void playSound(final int[] blockPos, final EntityCitizen worker)
    {
        worker.queueSound(SoundEvents.DEEPSLATE_TILES_HIT, blockPos, 5, 1, 1.0f, 2.0f);
    }
}





