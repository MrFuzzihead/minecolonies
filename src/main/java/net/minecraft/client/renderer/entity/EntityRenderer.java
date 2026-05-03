package net.minecraft.client.renderer.entity;

/** [1.7.10 stub] EntityRenderer */
public abstract class EntityRenderer<T>
{
    protected EntityRendererProvider.Context context;

    public EntityRenderer(EntityRendererProvider.Context context)
    {
        this.context = context;
    }

    public abstract net.minecraft.util.ResourceLocation getTextureLocation(T entity);

    public void render(T entity, double x, double y, double z, float yaw, float partialTick, Object poseStack, Object buffer, int packedLight) {}
    public boolean shouldRender(T entity, Object frustum, double x, double y, double z) { return true; }
}

