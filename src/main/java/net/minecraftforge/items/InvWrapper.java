package net.minecraftforge.items;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

/**
 * [1.7.10] Shim wrapping a player's InventoryPlayer (or any IInventory) as an IItemHandler.
 */
public class InvWrapper implements IItemHandlerModifiable
{
    private final InventoryPlayer inv;

    public InvWrapper(final InventoryPlayer inv)
    {
        this.inv = inv;
    }

    @Override
    public int getSlots()
    {
        return inv.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(final int slot)
    {
        return inv.getStackInSlot(slot);
    }

    @Override
    public ItemStack insertItem(final int slot, final ItemStack stack, final boolean simulate)
    {
        if (stack == null || stack.stackSize == 0)
        {
            return null;
        }
        final ItemStack existing = inv.getStackInSlot(slot);
        if (existing != null)
        {
            return stack; // slot occupied
        }
        if (!simulate)
        {
            inv.setInventorySlotContents(slot, stack.copy());
        }
        return null;
    }

    @Override
    public ItemStack extractItem(final int slot, final int amount, final boolean simulate)
    {
        final ItemStack existing = inv.getStackInSlot(slot);
        if (existing == null || amount <= 0)
        {
            return null;
        }
        final int toExtract = Math.min(amount, existing.stackSize);
        final ItemStack result = existing.copy();
        result.stackSize = toExtract;
        if (!simulate)
        {
            existing.stackSize -= toExtract;
            if (existing.stackSize <= 0)
            {
                inv.setInventorySlotContents(slot, null);
            }
        }
        return result;
    }

    @Override
    public int getSlotLimit(final int slot)
    {
        return inv.getInventoryStackLimit();
    }

    @Override
    public void setStackInSlot(final int slot, final ItemStack stack)
    {
        inv.setInventorySlotContents(slot, stack);
    }
}

