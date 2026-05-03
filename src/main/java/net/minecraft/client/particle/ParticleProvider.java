package net.minecraft.client.particle;

/** [1.7.10 stub] ParticleProvider */
@FunctionalInterface
public interface ParticleProvider<T>
{
    TextureSheetParticle createParticle(T type, Object level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed);
}

