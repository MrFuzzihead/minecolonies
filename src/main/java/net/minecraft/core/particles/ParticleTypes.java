package net.minecraft.core.particles;

/**
 * [1.7.10] Compatibility shim for 1.21 ParticleTypes.
 * In 1.7.10, particles are spawned via World.spawnParticle(String, ...) or EnumParticleTypes.
 */
public class ParticleTypes
{
    public static final String EXPLOSION = "explode";
    public static final String HEART = "heart";
    public static final String SMOKE = "smoke";

    private ParticleTypes() {}
}

