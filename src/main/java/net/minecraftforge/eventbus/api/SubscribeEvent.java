package net.minecraftforge.eventbus.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * [1.7.10] Compatibility shim for Forge 1.14+ @SubscribeEvent.
 * In 1.7.10, event handlers use @ForgeSubscribe from cpw.mods.fml.common.eventhandler.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SubscribeEvent
{
    EventPriority priority() default EventPriority.NORMAL;
    boolean receiveCanceled() default false;
}
