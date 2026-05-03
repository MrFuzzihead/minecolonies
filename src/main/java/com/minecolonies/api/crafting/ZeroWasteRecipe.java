package com.minecolonies.api.crafting;

// [1.7.10 BACKPORT STUB] ShapelessRecipe/Ingredient/CraftingContainer do not exist in 1.7.10.
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * [1.7.10 BACKPORT STUB] A shapeless recipe that discards remaining items.
 * In 1.7.10 there is no recipe type system; this is a no-op stub.
 */
public class ZeroWasteRecipe
{
    private final ResourceLocation id;
    private final ItemStack output;
    private final List<ItemStack> inputs;

    public ZeroWasteRecipe(@NotNull final ResourceLocation id,
                           @NotNull final ItemStack output,
                           @NotNull final List<ItemStack> inputs)
    {
        this.id = id;
        this.output = output;
        this.inputs = inputs;
    }

    public ResourceLocation getId() { return id; }
    public ItemStack getResultItem() { return output; }
    public List<ItemStack> getIngredients() { return inputs; }

    public static class Serializer
    {
        private static final Serializer INSTANCE = new Serializer();
        public static Serializer get() { return INSTANCE; }
        private Serializer() {}

        @Nullable
        public ZeroWasteRecipe fromNetwork(@NotNull final ResourceLocation id, @NotNull final PacketBuffer buf)
        {
            return null; // stub
        }

        public void toNetwork(@NotNull final PacketBuffer buf, @NotNull final ZeroWasteRecipe recipe)
        {
            // stub
        }
    }
}
