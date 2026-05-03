package net.minecraft.client.renderer;
/** [1.7.10 stub] RenderStateShard and its inner classes */
public abstract class RenderStateShard {
    public static class DepthTestStateShard extends RenderStateShard {
        public DepthTestStateShard(String name, int func) {}
        public static final DepthTestStateShard LEQUAL_DEPTH_TEST = new DepthTestStateShard("lequal", 515);
        public static final DepthTestStateShard NO_DEPTH_TEST = new DepthTestStateShard("never", 519);
        public static final DepthTestStateShard EQUAL_DEPTH_TEST = new DepthTestStateShard("equal", 514);
    }
    public static class TransparencyStateShard extends RenderStateShard {
        public TransparencyStateShard(String name, Runnable enable, Runnable disable) {}
        public static final TransparencyStateShard NO_TRANSPARENCY = new TransparencyStateShard("none", () -> {}, () -> {});
        public static final TransparencyStateShard TRANSLUCENT_TRANSPARENCY = new TransparencyStateShard("translucent", () -> {}, () -> {});
    }
    public static class ShaderStateShard extends RenderStateShard {
        public ShaderStateShard() {}
        public ShaderStateShard(java.util.function.Supplier<Object> shader) {}
    }
    public static class TextureStateShard extends RenderStateShard {
        public TextureStateShard() {}
        public TextureStateShard(net.minecraft.util.ResourceLocation texture, boolean blur, boolean mipmap) {}
    }
    public static class WriteMaskStateShard extends RenderStateShard {
        public WriteMaskStateShard(boolean color, boolean depth) {}
    }
    public static class LayeringStateShard extends RenderStateShard {
        public LayeringStateShard(String name, Runnable enable, Runnable disable) {}
        public static final LayeringStateShard NO_LAYERING = new LayeringStateShard("none", () -> {}, () -> {});
        public static final LayeringStateShard POLYGON_OFFSET_LAYERING = new LayeringStateShard("polygon_offset", () -> {}, () -> {});
    }
    public static class OutputStateShard extends RenderStateShard {
        public static final OutputStateShard MAIN_TARGET = new OutputStateShard();
    }
    public static class LightmapStateShard extends RenderStateShard {
        public static final LightmapStateShard NO_LIGHTMAP = new LightmapStateShard();
        public static final LightmapStateShard LIGHTMAP = new LightmapStateShard();
    }
    public static class OverlayStateShard extends RenderStateShard {
        public static final OverlayStateShard NO_OVERLAY = new OverlayStateShard();
        public static final OverlayStateShard OVERLAY = new OverlayStateShard();
    }
    public static class CullStateShard extends RenderStateShard {
        public static final CullStateShard CULL = new CullStateShard();
        public static final CullStateShard NO_CULL = new CullStateShard();
    }
}