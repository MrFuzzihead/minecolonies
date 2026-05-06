package net.minecraftforge.event.entity.living;

import net.minecraft.entity.EntityLiving;

/** [1.7.10 bridge] LivingConversionEvent */
public class LivingConversionEvent
{
    private final EntityLiving entity;

    public LivingConversionEvent(EntityLiving e) { this.entity = e; }
    public EntityLiving getEntity() { return entity; }

    public static class Pre extends LivingConversionEvent
    {
        public Pre(EntityLiving e) { super(e); }
    }

    public static class Post extends LivingConversionEvent
    {
        public Post(EntityLiving e) { super(e); }
    }
}

