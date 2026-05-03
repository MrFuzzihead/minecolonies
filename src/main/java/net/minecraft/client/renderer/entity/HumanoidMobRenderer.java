package net.minecraft.client.renderer.entity;

/** [1.7.10 stub] HumanoidMobRenderer - base renderer for humanoid mobs */
public abstract class HumanoidMobRenderer<T, M extends net.minecraft.client.model.HumanoidModel<T>>
{
    protected M model;

    public HumanoidMobRenderer(EntityRendererProvider.Context context, M model, float shadowSize)
    {
        this.model = model;
    }

    public void addLayer(Object layer) {}

    public void render(T entity, float limbSwing, float limbSwingAmount, Object poseStack, Object buffer, int packedLight) {}

    public net.minecraft.util.ResourceLocation getTextureLocation(T entity) { return null; }
}

