package com.mojang.blaze3d.vertex;

/**
 * [1.7.10] Compatibility stub for Blaze3D PoseStack (matrix stack for rendering).
 */
public class PoseStack
{
    /** [1.7.10 stub] PoseStack.Pose - matrix entry on the stack */
    public static class Pose
    {
        public Object pose() { return null; }
        public Object normal() { return null; }
    }

    public PoseStack() {}

    public void pushPose() {}
    public void popPose() {}
    public void translate(double x, double y, double z) {}
    public void scale(float x, float y, float z) {}
    public void mulPose(Object quaternion) {}
    public Pose last() { return new Pose(); }
}

