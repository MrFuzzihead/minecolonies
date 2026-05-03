package net.minecraft.client.renderer.texture;

/**
 * [1.7.10] Compatibility shim for DynamicTexture (exists in both 1.7.10 and 1.21).
 * Provides a basic compile stub.
 */
public class DynamicTexture
{
    private final int width;
    private final int height;

    public DynamicTexture(final int width, final int height)
    {
        this.width = width;
        this.height = height;
    }

    public DynamicTexture(final int width, final int height, final boolean useLinear)
    {
        this.width = width;
        this.height = height;
    }

    public void upload() {}

    public int[] getPixels()
    {
        return new int[width * height];
    }

    public void setPixels(final int[] pixels) {}
}

