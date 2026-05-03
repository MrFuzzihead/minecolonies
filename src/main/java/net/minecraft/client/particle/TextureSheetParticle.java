package net.minecraft.client.particle;

/** [1.7.10 stub] TextureSheetParticle - 1.21 particle base class */
public abstract class TextureSheetParticle
{
    protected double coordX, coordY, coordZ;
    protected double motionX, motionY, motionZ;
    protected float particleAlpha = 1.0f;
    protected int particleMaxAge;
    protected int particleAge;
    protected boolean isExpired = false;

    protected TextureSheetParticle() {}

    public void tick() {}
    public void renderParticle(Object buffer, Object renderInfo, float partialTicks) {}
    public abstract Object getRenderType();
    public boolean isAlive() { return !isExpired; }
    public void setLifetime(int lifetime) { this.particleMaxAge = lifetime; }
    public void setAlpha(float alpha) { this.particleAlpha = alpha; }
    public void setColor(float r, float g, float b) {}
    public void scale(float scale) {}
    public void setSpriteFromAge(Object sprites) {}
    public void pickSprite(Object sprites) {}
}

