package net.minecraft.client.gui.components;

/**
 * [1.7.10] Compatibility shim for 1.21 vanilla Button widget.
 * In 1.7.10, buttons are net.minecraft.client.gui.GuiButton.
 */
public class Button
{
    public static final Object DEFAULT_NARRATION = null;

    public boolean active = true;
    public boolean visible = true;
    public boolean isHovered = false;
    public int x;
    public int y;
    public int width;
    public int height;
    public interface OnPress
    {
        void onPress(Button button);
    }

    public Button(final int x, final int y, final int width, final int height, final Object message, final OnPress onPress)
    {
    }

    public Button(final int x, final int y, final int width, final int height, final Object message, final OnPress onPress, final Object narrationSupplier)
    {
    }

    public boolean isActive()
    {
        return active;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public void render(final Object guiGraphics, final int mouseX, final int mouseY, final float partialTicks) {}
    public void renderWidget(final Object guiGraphics, final int mouseX, final int mouseY, final float partialTicks) {}
    public void onPress() {}

    public void setMessage(final Object message) {}
    public void setTooltip(final Object tooltip) {}
    public void setY(final int y) { this.y = y; }
    public String getID() { return ""; }
}



