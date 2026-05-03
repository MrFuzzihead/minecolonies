package net.minecraftforge.items;

import net.minecraft.item.ItemStack;

/**
 * Compatibility shim: replaces the 1.21 Forge Capability IItemHandler interface
 * for the MineColonies 1.7.10 backport.
 */
public interface IItemHandler
{
    int getSlots();

    ItemStack getStackInSlot(int slot);

    ItemStack insertItem(int slot, ItemStack stack, boolean simulate);

    ItemStack extractItem(int slot, int amount, boolean simulate);

    int getSlotLimit(int slot);

    boolean isItemValid(int slot, ItemStack stack);
}

