package net.minecraft.client.renderer.entity.layers;

/** [1.7.10 stub] RenderLayer - entity rendering layer */
public abstract class RenderLayer<T, M>
{
    protected final RenderLayerParent<T, M> renderer;

    public RenderLayer(RenderLayerParent<T, M> renderer)
    {
        this.renderer = renderer;
    }

    public abstract void render(Object poseStack, Object buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch);

    public M getParentModel() { return renderer.getModel(); }
}

