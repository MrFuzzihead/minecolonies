package net.minecraft.world;

import net.minecraft.item.ItemStack;

/**
 * [1.7.10] Compatibility shim for 1.21 SimpleContainer.
 * A simple in-memory inventory implementation.
 */
public class SimpleContainer implements Container
{
    private final ItemStack[] items;

    public SimpleContainer(final int size)
    {
        this.items = new ItemStack[size];
    }

    @Override public int getContainerSize() { return items.length; }
    @Override public ItemStack getItem(final int index) { return index < items.length ? items[index] : null; }
    @Override public ItemStack removeItem(final int index, final int count) { return null; }
    @Override public ItemStack removeItemNoUpdate(final int index) { return null; }
    @Override public void setItem(final int index, final ItemStack stack) { if (index < items.length) items[index] = stack; }
    @Override public boolean stillValid(final Object player) { return true; }
    @Override public void clearContent() {}
}

