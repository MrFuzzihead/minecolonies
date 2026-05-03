package net.minecraft.client.renderer.entity;

/** [1.7.10 stub] EntityRendererProvider */
public interface EntityRendererProvider<T>
{
    /** [1.7.10 stub] Context provides renderer dependencies */
    class Context
    {
        public Object getItemInHandRenderer() { return null; }
        public Object getModelManager() { return null; }
        public net.minecraft.client.model.geom.ModelPart bakeLayer(net.minecraft.client.model.geom.ModelLayerLocation layer) { return new net.minecraft.client.model.geom.ModelPart(); }
    }

    Object create(Context context);
}

