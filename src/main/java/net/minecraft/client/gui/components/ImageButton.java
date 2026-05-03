package net.minecraft.client.gui.components;

import net.minecraft.util.ResourceLocation;

/**
 * [1.7.10] Compatibility shim for 1.21 ImageButton.
 */
public class ImageButton extends Button
{
    public ImageButton(final int x, final int y, final int width, final int height,
                       final int uOffset, final int vOffset, final int yDiffTex,
                       final ResourceLocation resourceLocation, final OnPress onPress)
    {
        super(x, y, width, height, null, onPress);
    }
}

