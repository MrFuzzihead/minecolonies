package com.minecolonies.api.crafting;

// [1.7.10 BACKPORT] Ingredient API (net.minecraft.world.item.crafting.Ingredient) does not exist in 1.7.10.
// This class is stubbed. CountedIngredient is a concept from the 1.21 recipe system.
import com.minecolonies.api.util.constant.Constants;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * [1.7.10 BACKPORT STUB] An ingredient that requires more than one item in a slot.
 * In 1.7.10 there is no Ingredient abstraction; this class acts as a simple wrapper.
 */
public class CountedIngredient
{
    public static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "counted");

    @NotNull
    private final ItemStack[] stacks;
    private final int count;

    public CountedIngredient(@NotNull final ItemStack[] stacks, final int count)
    {
        this.stacks = stacks;
        this.count = count;
    }

    public int getCount() { return count; }

    public ItemStack[] getItems() { return stacks; }

    public boolean test(final ItemStack stack)
    {
        for (final ItemStack s : stacks)
        {
            if (s.isItemEqual(stack) && stack.stackSize >= count) return true;
        }
        return false;
    }

    public static class Serializer
    {
        private static final Serializer INSTANCE = new Serializer();
        public static Serializer getInstance() { return INSTANCE; }
        private Serializer() {}

        public CountedIngredient parse(@NotNull final PacketBuffer buffer)
        {
            final int count = buffer.readVarInt();
            final int stackCount = buffer.readVarInt();
            final ItemStack[] stacks = new ItemStack[stackCount];
            for (int i = 0; i < stackCount; i++) stacks[i] = buffer.readItemStack();
            return new CountedIngredient(stacks, count);
        }

        public void write(@NotNull final PacketBuffer buffer, @NotNull final CountedIngredient ingredient)
        {
            buffer.writeVarInt(ingredient.getCount());
            buffer.writeVarInt(ingredient.getItems().length);
            for (final ItemStack s : ingredient.getItems()) buffer.writeItemStack(s);
        }
    }
}
