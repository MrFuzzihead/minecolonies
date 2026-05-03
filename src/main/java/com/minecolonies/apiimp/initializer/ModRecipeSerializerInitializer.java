package com.minecolonies.apiimp.initializer;

import com.minecolonies.api.crafting.ZeroWasteRecipe;
import com.minecolonies.api.crafting.CompostRecipe;
import com.minecolonies.api.crafting.registry.ModRecipeSerializer;
import com.minecolonies.api.util.constant.Constants;
// [1.7.10] RecipeSerializer/RecipeType from net.minecraft.world.item.crafting not available
import net.minecraft.util.ResourceLocation;

public final class ModRecipeSerializerInitializer
{
    // [1.7.10] ForgeRegistries.RECIPE_SERIALIZERS / Registries.RECIPE_TYPE not available
    public static final DeferredRegister<Object> RECIPE_SERIALIZER = DeferredRegister.create(new ResourceLocation(Constants.MOD_ID, "recipe_serializers"), Constants.MOD_ID);
    public static final DeferredRegister<Object> RECIPE_TYPES      = DeferredRegister.create(new ResourceLocation(Constants.MOD_ID, "recipe_types"), Constants.MOD_ID);

    static
    {
        ModRecipeSerializer.CompostRecipeSerializer = RECIPE_SERIALIZER.register("composting", CompostRecipe.Serializer::new);
        ModRecipeSerializer.CompostRecipeType = RECIPE_TYPES.register("composting", () -> null /* [1.7.10] RecipeType.simple not available */);

        ModRecipeSerializer.ZeroWasteRecipeSerializer = RECIPE_SERIALIZER.register("zero_waste", ZeroWasteRecipe.Serializer::new);
    }

    private ModRecipeSerializerInitializer()
    {
        throw new IllegalStateException("Tried to initialize: ModRecipeSerializerInitializer but this is a Utility class.");
    }
}
