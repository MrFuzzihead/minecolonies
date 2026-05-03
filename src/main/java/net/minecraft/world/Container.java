package net.minecraft.world;

import net.minecraft.item.ItemStack;

/**
 * [1.7.10] Compatibility shim for 1.21 Container interface (inventory container).
 * In 1.7.10, this is net.minecraft.inventory.IInventory.
 */
public interface Container
{
    int getContainerSize();
    ItemStack getItem(int index);
    ItemStack removeItem(int index, int count);
    ItemStack removeItemNoUpdate(int index);
    void setItem(int index, ItemStack stack);
    boolean stillValid(Object player);
    void clearContent();
}

