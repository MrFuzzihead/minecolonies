package com.mojang.blaze3d.systems;

/** [1.7.10 stub] RenderSystem - opengl render state management */
public class RenderSystem
{
    public static void enableBlend() {}
    public static void disableBlend() {}
    public static void enableDepthTest() {}
    public static void disableDepthTest() {}
    public static void setShaderColor(float r, float g, float b, float a) {}
    public static void setShaderTexture(int unit, net.minecraft.util.ResourceLocation texture) {}
    public static void setShaderTexture(int unit, int textureId) {}
    public static void applyModelViewMatrix() {}
    public static Object getModelViewMatrix() { return null; }
    public static Object getProjectionMatrix() { return null; }
    public static void blendFunc(int src, int dst) {}
    public static void defaultBlendFunc() {}
    public static void assertOnRenderThread() {}
}

