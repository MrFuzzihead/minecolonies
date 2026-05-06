package net.minecraft.world.item.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;

/** [1.7.10 bridge] Ingredient - maps to net.minecraft.item.crafting.Ingredient concept */
public class Ingredient
{
    /** [1.7.10 stub] Ingredient.Value inner interface */
    public interface Value
    {
        java.util.Collection<ItemStack> getItems();
    }

    public static final Ingredient EMPTY = new Ingredient();

    public static Ingredient of() { return new Ingredient(); }
    public static Ingredient of(ItemStack... stacks) { return new Ingredient(); }

    public com.google.gson.JsonElement toJson() { return new com.google.gson.JsonObject(); }

    public boolean isEmpty() { return false; }
    public boolean test(ItemStack stack) { return false; }
}

