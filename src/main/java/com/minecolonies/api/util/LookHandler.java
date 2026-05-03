package com.minecolonies.api.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
// [1.7.10] LookControl -> EntityLookHelper
import net.minecraft.entity.ai.EntityLookHelper;

/**
 * [1.7.10] Replaces 1.21 LookControl-based LookHandler with EntityLookHelper.
 */
public class LookHandler extends EntityLookHelper
{
    private boolean doneNavigating = true;
    private Entity  lookingAt      = null;
    private int     lookAtTimer    = 0;

    public LookHandler(final EntityCreature entity)
    {
        super(entity);
    }

    /** [1.7.10] Called from updateEntity instead of tick(). */
    public void onUpdate()
    {
        // [1.7.10] EntityLookHelper updates via onUpdateLook(); no direct tick override
        if (lookingAt != null && lookAtTimer-- > 0)
        {
            setLookPositionWithEntity(lookingAt, 10.0F, 40.0F);
            if (lookAtTimer == 0)
            {
                lookingAt = null;
            }
        }
    }

    public void setLookAtCooldown(final int cooldown)
    {
        lookAtTimer = cooldown;
    }

    /** Look at an entity for ~5 seconds. */
    public void setLookAt(final Entity entity)
    {
        setLookPositionWithEntity(entity, 10.0F, 40.0F);
        lookAtTimer = 20 * 5;
        lookingAt = entity;
    }
}
