package net.minecraft.world.entity.player;

import net.minecraft.item.ItemStack;

/**
 * [1.7.10] Compatibility shim for 1.21 Inventory (player inventory class).
 * In 1.7.10, this is net.minecraft.entity.player.InventoryPlayer.
 */
public class Inventory
{
    public Inventory() {}

    public ItemStack getItem(final int index)
    {
        return null;
    }

    public int getContainerSize()
    {
        return 36;
    }
}

