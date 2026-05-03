package net.minecraftforge.items;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Compatibility shim: replaces the 1.21 Forge Capability SlotItemHandler.
 * Wraps an IItemHandler (itself a shim) as a vanilla Slot using an adapter IInventory.
 */
public class SlotItemHandler extends Slot
{
    private final IItemHandler itemHandler;
    private final int handlerSlot;

    public SlotItemHandler(final IItemHandler itemHandler, final int handlerSlot, final int xPos, final int yPos)
    {
        super(new IInventoryAdapter(itemHandler), handlerSlot, xPos, yPos);
        this.itemHandler = itemHandler;
        this.handlerSlot = handlerSlot;
    }

    @Override
    public ItemStack getStack()
    {
        return itemHandler.getStackInSlot(handlerSlot);
    }

    @Override
    public void putStack(final ItemStack stack)
    {
        if (stack == null || stack.stackSize == 0)
        {
            itemHandler.extractItem(handlerSlot, Integer.MAX_VALUE, false);
        }
        else if (itemHandler instanceof IItemHandlerModifiable)
        {
            ((IItemHandlerModifiable) itemHandler).setStackInSlot(handlerSlot, stack);
        }
        this.onSlotChanged();
    }

    @Override
    public boolean isItemValid(final ItemStack stack)
    {
        return itemHandler.isItemValid(handlerSlot, stack);
    }

    @Override
    public boolean getHasStack()
    {
        final ItemStack stack = itemHandler.getStackInSlot(handlerSlot);
        return stack != null && stack.stackSize > 0;
    }

    @Override
    public int getSlotStackLimit()
    {
        return itemHandler.getSlotLimit(handlerSlot);
    }

    /** Minimal IInventory adapter wrapping an IItemHandler. */
    private static final class IInventoryAdapter implements IInventory
    {
        private final IItemHandler handler;

        IInventoryAdapter(final IItemHandler handler)
        {
            this.handler = handler;
        }

        @Override public int getSizeInventory() { return handler.getSlots(); }
        @Override public ItemStack getStackInSlot(final int slot) { return handler.getStackInSlot(slot); }
        @Override public ItemStack decrStackSize(final int slot, final int amount) { return handler.extractItem(slot, amount, false); }
        @Override public ItemStack getStackInSlotOnClosing(final int slot) { return null; }
        @Override public void setInventorySlotContents(final int slot, final ItemStack stack)
        {
            if (handler instanceof IItemHandlerModifiable)
            {
                ((IItemHandlerModifiable) handler).setStackInSlot(slot, stack);
            }
        }
        @Override public String getInventoryName() { return "IItemHandler"; }
        @Override public boolean hasCustomInventoryName() { return false; }
        @Override public int getInventoryStackLimit() { return 64; }
        @Override public void markDirty() {}
        @Override public boolean isUseableByPlayer(final net.minecraft.entity.player.EntityPlayer player) { return true; }
        @Override public void openInventory() {}
        @Override public void closeInventory() {}
        @Override public boolean isItemValidForSlot(final int slot, final ItemStack stack) { return handler.isItemValid(slot, stack); }
    }
}

