package net.minecraft.client.model;

/**
 * [1.7.10] Compatibility stub for 1.21 CubeDeformation.
 */
public class CubeDeformation
{
    public static final CubeDeformation NONE = new CubeDeformation(0.0F);

    private final float growX;
    private final float growY;
    private final float growZ;

    public CubeDeformation(float growX, float growY, float growZ)
    {
        this.growX = growX;
        this.growY = growY;
        this.growZ = growZ;
    }

    public CubeDeformation(float grow)
    {
        this(grow, grow, grow);
    }
}

