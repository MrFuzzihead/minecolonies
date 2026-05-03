package net.minecraft.client.renderer.entity.layers;

/** [1.7.10 stub] RenderLayerParent - parent of a render layer */
public interface RenderLayerParent<T, M>
{
    M getModel();
    net.minecraft.util.ResourceLocation getTextureLocation(T entity);
}

