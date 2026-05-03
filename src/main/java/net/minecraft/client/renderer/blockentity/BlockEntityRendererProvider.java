package net.minecraft.client.renderer.blockentity;

/** [1.7.10 stub] BlockEntityRendererProvider */
public interface BlockEntityRendererProvider<T>
{
    class Context
    {
        public Object getBlockEntityRenderDispatcher() { return null; }
    }

    BlockEntityRenderer<T> create(Context context);
}

