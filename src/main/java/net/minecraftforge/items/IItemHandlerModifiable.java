package net.minecraftforge.items;
import net.minecraft.item.ItemStack;
/** [1.7.10 shim] IItemHandlerModifiable - replaces Forge 1.8+ capability-based item handler. */
public interface IItemHandlerModifiable {
    int getSlots();
    ItemStack getStackInSlot(int slot);
    ItemStack insertItem(int slot, ItemStack stack, boolean simulate);
    ItemStack extractItem(int slot, int amount, boolean simulate);
    int getSlotLimit(int slot);
    boolean isItemValid(int slot, ItemStack stack);
    void setStackInSlot(int slot, ItemStack stack);
}
