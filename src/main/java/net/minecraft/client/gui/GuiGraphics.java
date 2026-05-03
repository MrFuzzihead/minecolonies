package net.minecraft.client.gui;

import net.minecraft.util.ResourceLocation;

/**
 * [1.7.10] Compatibility shim for 1.21 GuiGraphics rendering context.
 * In 1.7.10, rendering is done via direct OpenGL calls and Tessellator.
 */
public class GuiGraphics
{
    public GuiGraphics() {}

    public void blit(final ResourceLocation texture, final int x, final int y, final int u, final int v, final int w, final int h) {}

    public void blit(final ResourceLocation texture, final int x, final int y, final float u, final float v, final int w, final int h,
                     final int textureW, final int textureH) {}

    public void renderItem(final Object itemStack, final int x, final int y) {}

    public void drawString(final Object font, final String text, final int x, final int y, final int color) {}

    public void fill(final int x1, final int y1, final int x2, final int y2, final int color) {}
}

