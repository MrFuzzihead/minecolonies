package net.minecraftforge.event.entity.living;

import net.minecraft.util.DamageSource;
import net.minecraft.entity.EntityLivingBase;

/** [1.7.10 bridge] LivingDeathEvent - maps to Forge 1.7.10 LivingDeathEvent */
public class LivingDeathEvent
{
    private final EntityLivingBase entity;
    private final DamageSource source;

    public LivingDeathEvent(final EntityLivingBase entity, final DamageSource source)
    {
        this.entity = entity;
        this.source = source;
    }

    public EntityLivingBase getEntity() { return entity; }
    public DamageSource getSource() { return source; }
    public boolean isCanceled() { return false; }
    public void setCanceled(boolean canceled) {}
}

