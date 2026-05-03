package net.minecraft.world;

/**
 * [1.7.10] Compatibility stub for 1.21 Rotation enum.
 */
public enum Rotation
{
    NONE,
    CLOCKWISE_90,
    CLOCKWISE_180,
    COUNTERCLOCKWISE_90;

    public Rotation getRotated(Rotation other) { return this; }
}

