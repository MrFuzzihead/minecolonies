package net.minecraft.world.item.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;

/** [1.7.10 bridge] Ingredient - maps to net.minecraft.item.crafting.Ingredient concept */
public class Ingredient
{
    public static Ingredient of() { return new Ingredient(); }
    public static Ingredient of(ItemStack... stacks) { return new Ingredient(); }

    public JsonElement toJson() { return new JsonObject(); }

    public boolean isEmpty() { return false; }
    public boolean test(ItemStack stack) { return false; }
}

