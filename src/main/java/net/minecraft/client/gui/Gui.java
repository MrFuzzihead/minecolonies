package net.minecraft.client.gui;

import net.minecraft.util.ResourceLocation;

/**
 * [1.7.10] Compatibility shim for net.minecraft.client.gui.Gui.
 * Provides GUI_ICONS_LOCATION static field for 1.21 compatibility.
 */
public class Gui
{
    public static final ResourceLocation GUI_ICONS_LOCATION = new ResourceLocation("textures/gui/icons.png");

    public Gui() {}
}

