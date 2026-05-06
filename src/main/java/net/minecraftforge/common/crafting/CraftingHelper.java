package net.minecraftforge.common.crafting;

import com.google.gson.JsonElement;
import net.minecraft.world.item.crafting.Ingredient;

/** [1.7.10 bridge] CraftingHelper - partial equivalent in OreDictionary */
public class CraftingHelper
{
    public static Ingredient getIngredient(final JsonElement element, final boolean allowEmpty)
    {
        return new Ingredient();
    }
}

