package net.minecraftforge.fml.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * [1.7.10] Compatibility shim for Forge 1.8+ @Mod annotation.
 * In 1.7.10, @Mod is in cpw.mods.fml.common.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Mod
{
    String value() default "";
    String modid() default "";
    String name() default "";
    String version() default "";

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface EventBusSubscriber
    {
        enum Bus
        {
            FORGE, MOD
        }

        Bus bus() default Bus.FORGE;
        net.minecraftforge.api.distmarker.Dist[] value() default {};
        String modid() default "";
        boolean client() default false;
        boolean dedicated_server() default false;
    }
}

