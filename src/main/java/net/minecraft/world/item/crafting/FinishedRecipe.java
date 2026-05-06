package net.minecraft.world.item.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;

/** [1.7.10 bridge] FinishedRecipe - no 1.7.10 equivalent */
public interface FinishedRecipe
{
    void serializeRecipeData(JsonObject json);
    RecipeSerializer<?> getType();
    ResourceLocation getId();
    JsonObject serializeAdvancement();
    ResourceLocation getAdvancementId();

    default JsonElement serializeRecipe()
    {
        final JsonObject json = new JsonObject();
        serializeRecipeData(json);
        return json;
    }
}

