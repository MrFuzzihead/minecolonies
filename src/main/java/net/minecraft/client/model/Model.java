package net.minecraft.client.model;

import java.util.function.Function;
import net.minecraft.util.ResourceLocation;

/** [1.7.10 stub] net.minecraft.client.model.Model - base class for non-humanoid models */
public abstract class Model
{
    /** [1.7.10] RenderType not available - use null */
    public Model() {}

    public Model(Function<ResourceLocation, Object> renderType) {}

    public abstract void renderToBuffer(Object poseStack, Object vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha);

    // Common render method overload
    public void renderToBuffer(Object poseStack, Object vertexConsumer, int packedLight, int packedOverlay) {
        renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, 1f, 1f, 1f, 1f);
    }
}

