package net.minecraft.client.model;

/** [1.7.10 stub] HorseModel */
public class HorseModel<T> extends Model
{
    public HorseModel() {}
    public HorseModel(net.minecraft.client.model.geom.ModelPart root) {}

    @Override
    public void renderToBuffer(Object poseStack, Object vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {}

    public void setupAnim(Object entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}
}

