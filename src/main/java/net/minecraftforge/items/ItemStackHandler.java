package net.minecraftforge.items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.INBTSerializable;
/** [1.7.10 shim] ItemStackHandler - replaces Forge 1.8+ capability-based item handler. */
public class ItemStackHandler implements IItemHandlerModifiable, INBTSerializable<NBTTagCompound> {
    protected ItemStack[] stacks;
    public ItemStackHandler() { this(1); }
    public ItemStackHandler(final int size) { this.stacks = new ItemStack[size]; }
    @Override public int getSlots() { return stacks.length; }
    @Override public ItemStack getStackInSlot(int slot) { return stacks[slot]; }
    @Override public void setStackInSlot(int slot, ItemStack stack) { stacks[slot] = stack; onContentsChanged(slot); }
    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack == null) return null;
        ItemStack existing = stacks[slot];
        if (existing != null) return stack;
        if (!simulate) { stacks[slot] = stack.copy(); onContentsChanged(slot); }
        return null;
    }
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack existing = stacks[slot];
        if (existing == null || amount <= 0) return null;
        ItemStack out = existing.copy();
        out.stackSize = Math.min(out.stackSize, amount);
        if (!simulate) { existing.stackSize -= out.stackSize; if (existing.stackSize <= 0) stacks[slot] = null; onContentsChanged(slot); }
        return out;
    }
    @Override public int getSlotLimit(int slot) { return 64; }
    @Override public boolean isItemValid(int slot, ItemStack stack) { return true; }
    protected void onContentsChanged(int slot) {}
    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagList list = new NBTTagList();
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("Items", list);
        nbt.setInteger("Size", stacks.length);
        return nbt;
    }
    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        int size = nbt.hasKey("Size") ? nbt.getInteger("Size") : stacks.length;
        stacks = new ItemStack[size];
    }
    public int getSlotCount() { return stacks.length; }
}
