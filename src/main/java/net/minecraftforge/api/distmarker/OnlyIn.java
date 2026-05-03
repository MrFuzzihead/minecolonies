package net.minecraftforge.api.distmarker;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * [1.7.10] Compatibility shim for Forge 1.13+ @OnlyIn annotation.
 * In 1.7.10, use @SideOnly(Side.CLIENT) from cpw.mods.fml.relauncher.SideOnly.
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
public @interface OnlyIn
{
    Dist value();
}

