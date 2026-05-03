package net.minecraft.client.particle;

/** [1.7.10 stub] ParticleRenderType */
public interface ParticleRenderType
{
    ParticleRenderType PARTICLE_SHEET_TRANSLUCENT = new ParticleRenderType() {};
    ParticleRenderType PARTICLE_SHEET_OPAQUE = new ParticleRenderType() {};
    ParticleRenderType TERRAIN_SHEET = new ParticleRenderType() {};
    ParticleRenderType NO_RENDER = new ParticleRenderType() {};

    default void begin(Object builder, Object texture) {}
    default void end(Object tessellator) {}
}

