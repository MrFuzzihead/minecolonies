package net.minecraft.client.model.geom;

/**
 * [1.7.10] Compatibility stub for 1.21 ModelPart (a part of a model).
 */
public class ModelPart
{
    public boolean visible = true;
    public ModelPart() {}

    public void render(Object poseStack, Object buffer, int packedLight, int packedOverlay) {}
    public void render(Object poseStack, Object buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {}
    public ModelPart getChild(String name) { return this; }
    public void translateAndRotate(Object poseStack) {}
}

