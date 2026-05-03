package net.minecraft.client.renderer.entity.layers;

/** [1.7.10 stub] ItemInHandLayer */
public class ItemInHandLayer<T, M> extends RenderLayer<T, M>
{
    public ItemInHandLayer(RenderLayerParent<T, M> renderer, Object itemRenderer)
    {
        super(renderer);
    }

    @Override
    public void render(Object poseStack, Object buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {}
}

