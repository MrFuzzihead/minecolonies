package net.minecraft.client.renderer.entity.layers;

/** [1.7.10 stub] HumanoidArmorLayer */
public class HumanoidArmorLayer<T, M extends net.minecraft.client.model.HumanoidModel<T>, A extends net.minecraft.client.model.HumanoidModel<T>> extends RenderLayer<T, M>
{
    public HumanoidArmorLayer(RenderLayerParent<T, M> renderer, A innerModel, A outerModel, Object modelManager)
    {
        super(renderer);
    }

    @Override
    public void render(Object poseStack, Object buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {}
}

