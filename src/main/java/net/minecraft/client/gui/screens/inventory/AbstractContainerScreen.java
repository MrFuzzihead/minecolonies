package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.screens.Screen;

/**
 * [1.7.10] Compatibility shim for 1.21 AbstractContainerScreen.
 * In 1.7.10, GUI container screens extend net.minecraft.client.gui.inventory.GuiContainer.
 *
 * @param <T> the container type.
 */
public abstract class AbstractContainerScreen<T> extends Screen
{
    protected final T menu;

    protected AbstractContainerScreen(final T menu, final Object playerInventory, final Object title)
    {
        this.menu = menu;
    }

    protected void renderBg(final Object guiGraphics, final float partialTick, final int mouseX, final int mouseY) {}

    @Override
    public void render(final Object guiGraphics, final int mouseX, final int mouseY, final float partialTick)
    {
        renderBg(guiGraphics, partialTick, mouseX, mouseY);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}

