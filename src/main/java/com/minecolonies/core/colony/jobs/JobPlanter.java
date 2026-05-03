package com.minecolonies.core.colony.jobs;

import net.minecraft.util.ResourceLocation;
import com.minecolonies.api.client.render.modeltype.ModModelTypes;
import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.core.entity.ai.workers.production.agriculture.EntityAIWorkPlanter;
// [1.7.10] net.minecraft.util.DamageSource removed
// [1.7.10] net.minecraft.util.DamageSource removed
import org.jetbrains.annotations.NotNull;

/**
 * Class of the planter job.
 */
public class JobPlanter extends AbstractJobCrafter<EntityAIWorkPlanter, JobPlanter>
{
    /**
     * Instantiates the job for the plantation.
     *
     * @param entity the citizen who becomes a planter
     */
    public JobPlanter(final ICitizenData entity)
    {
        super(entity);
    }

    @NotNull
    @Override
    public ResourceLocation getModel()
    {
        return ModModelTypes.PLANTER_ID;
    }

    /**
     * Generate your AI class to register.
     *
     * @return your personal AI instance.
     */
    @NotNull
    @Override
    public EntityAIWorkPlanter generateAI()
    {
        return new EntityAIWorkPlanter(this);
    }

    @Override
    public boolean ignoresDamage(@NotNull final net.minecraft.util.DamageSource source)
    {
        return net.minecraft.util.DamageSource.typeHolder().is(DamageTypes.CACTUS);
    }
}




