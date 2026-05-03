package net.minecraft.client.model;

/**
 * [1.7.10] Compatibility stub for 1.21 PartPose (position and rotation for model parts).
 */
public class PartPose
{
    public static final PartPose ZERO = offset(0, 0, 0);

    public final float x, y, z, xRot, yRot, zRot;

    private PartPose(float x, float y, float z, float xRot, float yRot, float zRot)
    {
        this.x = x; this.y = y; this.z = z;
        this.xRot = xRot; this.yRot = yRot; this.zRot = zRot;
    }

    public static PartPose offset(float x, float y, float z) { return new PartPose(x, y, z, 0, 0, 0); }
    public static PartPose offsetAndRotation(float x, float y, float z, float xRot, float yRot, float zRot)
    {
        return new PartPose(x, y, z, xRot, yRot, zRot);
    }
    public static PartPose rotation(float xRot, float yRot, float zRot) { return new PartPose(0, 0, 0, xRot, yRot, zRot); }
}

