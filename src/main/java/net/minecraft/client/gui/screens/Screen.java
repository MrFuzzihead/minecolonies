package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.components.Renderable;

/**
 * [1.7.10] Compatibility shim for 1.21 Screen base class.
 */
public abstract class Screen
{
    public int width;
    public int height;
    public Object font = null;
    protected net.minecraft.client.Minecraft minecraft = null;

    protected Screen() {}
    protected Screen(Object title) {}

    public void render(final Object guiGraphics, final int mouseX, final int mouseY, final float partialTick) {}
    protected void init() {}
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) { return false; }
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) { return false; }
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double delta) { return false; }
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) { return false; }
    public void onClose() {}

    protected <T> T addRenderableWidget(T widget) { return widget; }
    public void renderBackground(Object guiGraphics) {}
}
