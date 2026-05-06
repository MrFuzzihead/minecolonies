package com.minecolonies.api.crafting;

import com.minecolonies.api.items.ModItems;
import com.minecolonies.api.tileentities.AbstractTileEntityBarrel;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

/**
 * [1.7.10] CompostRecipe — plain data class (no IRecipe in 1.7.10).
 * Originally implements Recipe<Container>; recipe system replaced with custom map-based registry.
 */
public class CompostRecipe
{
    private static final int FERMENT_TIME = 24000;
    private static final int COMPOST_RESULT = 6;

    private final ResourceLocation id;

    /** Items that are accepted as input; exact match by item type. */
    private final Item[] inputs;
    private final ItemStack output;
    private final int strength;

    public CompostRecipe(@NotNull final ResourceLocation id, @NotNull final Item[] inputs, final int strength)
    {
        this.id = id;
        this.inputs = inputs;
        this.strength = strength;
        this.output = new ItemStack(ModItems.compost, COMPOST_RESULT);
    }

    /** Single-item convenience constructor. */
    public CompostRecipe(@NotNull final ResourceLocation id, @NotNull final Item input, final int strength)
    {
        this(id, new Item[]{input}, strength);
    }

    @NotNull
    public ResourceLocation getId() { return this.id; }

    /**
     * Get the accepted input items for this recipe.
     */
    public Item[] getInputItems() { return this.inputs; }

    /** Returns true if the given stack is a valid input. */
    public boolean matches(@NotNull final ItemStack stack)
    {
        for (final Item item : inputs) { if (item == stack.getItem()) return true; }
        return false;
    }

    /**
     * Get the strength of this recipe.
     */
    public int getStrength() { return this.strength; }

    /**
     * The number of ticks that this recipe should take to ferment.
     */
    public int getFermentTime() { return FERMENT_TIME; }

    @NotNull
    public ItemStack getResultItem() { return this.output.copy(); }

    private int calculateIngredientCount()
    {
        return AbstractTileEntityBarrel.MAX_ITEMS / this.strength;
    }

    // JEI: render as many individual recipes rather than one with many alternatives.
    @NotNull
    public static CompostRecipe individualize(@NotNull final Item item, @NotNull final CompostRecipe recipe)
    {
        return new CompostRecipe(recipe.getId(), item, recipe.getStrength());
    }

    /** Network serialisation. */
    public void toNetwork(@NotNull final PacketBuffer buffer)
    {
        buffer.writeInt(inputs.length);
        for (final Item item : inputs)
        {
            buffer.writeInt(Item.getIdFromItem(item));
        }
        buffer.writeInt(strength);
    }

    @Nullable
    public static CompostRecipe fromNetwork(@NotNull final ResourceLocation recipeId, @NotNull final PacketBuffer buffer)
    {
        final int count = buffer.readInt();
        final Item[] items = new Item[count];
        for (int i = 0; i < count; i++)
        {
            items[i] = Item.getItemById(buffer.readInt());
        }
        final int strength = buffer.readInt();
        return new CompostRecipe(recipeId, items, strength);
    }

    /**
     * [1.7.10] Serializer wrapper class (mirrors RecipeSerializer pattern for RegistryObject compatibility).
     */
    public static class Serializer
    {
        public void toNetwork(@NotNull final PacketBuffer buffer, @NotNull final CompostRecipe recipe)
        {
            recipe.toNetwork(buffer);
        }

        @Nullable
        public CompostRecipe fromNetwork(@NotNull final ResourceLocation id, @NotNull final PacketBuffer buffer)
        {
            return CompostRecipe.fromNetwork(id, buffer);
        }
    }
}

