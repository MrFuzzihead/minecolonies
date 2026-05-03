package net.minecraft.client.renderer.blockentity;

/** [1.7.10 stub] BlockEntityRenderer */
public interface BlockEntityRenderer<T>
{
    void render(T blockEntity, float partialTick, Object poseStack, Object bufferSource, int packedLight, int packedOverlay);

    default boolean shouldRenderOffScreen(T blockEntity) { return false; }
    default int getViewDistance() { return 64; }
}

