package net.minecraft.world.item;

/**
 * [1.7.10] Compatibility shim for 1.21 TooltipFlag.
 */
public interface TooltipFlag
{
    boolean isAdvanced();

    TooltipFlag NORMAL = () -> false;
    TooltipFlag ADVANCED = () -> true;
}

