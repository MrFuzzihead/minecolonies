package net.minecraft.client.renderer.entity;

/** [1.7.10 stub] MobRenderer - base renderer for mobs */
public abstract class MobRenderer<T, M> extends HumanoidMobRenderer<T, net.minecraft.client.model.HumanoidModel<T>>
{
    public MobRenderer(EntityRendererProvider.Context context, net.minecraft.client.model.HumanoidModel<T> model, float shadowSize)
    {
        super(context, model, shadowSize);
    }
}

