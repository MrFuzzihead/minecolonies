package net.minecraft.sounds;

import net.minecraft.util.ResourceLocation;

/**
 * [1.7.10] Shim replacing 1.21 SoundEvent.
 * In 1.7.10 sounds are referenced by string resource location.
 * This shim wraps a ResourceLocation for compatibility.
 */
public class SoundEvent
{
    private final ResourceLocation location;

    public SoundEvent(final ResourceLocation location)
    {
        this.location = location;
    }

    public ResourceLocation getLocation()
    {
        return location;
    }

    /** Factory for variable-range events (same as fixed in 1.7.10) */
    public static SoundEvent createVariableRangeEvent(final ResourceLocation loc)
    {
        return new SoundEvent(loc);
    }

    /** Factory for fixed-range events */
    public static SoundEvent createFixedRangeEvent(final ResourceLocation loc, final float range)
    {
        return new SoundEvent(loc);
    }

    /** Returns the sound name string used by 1.7.10 play calls */
    public String getSoundName()
    {
        return location.toString();
    }

    @Override
    public String toString()
    {
        return location.toString();
    }
}

