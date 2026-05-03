package net.minecraft.client.particle;

/** [1.7.10 stub] Particle */
public abstract class Particle
{
    protected double posX, posY, posZ;
    protected int particleMaxAge;
    protected int particleAge;
    protected boolean isExpired;

    public boolean isAlive() { return !isExpired; }
    public void tick() {}
    public void renderParticle(Object buffer, Object renderInfo, float partialTicks) {}
    public abstract Object getRenderType();
}

