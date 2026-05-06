package mezz.jei.api.constants;

import net.minecraft.util.ResourceLocation;

/** [1.7.10 stub] RecipeTypes — known JEI recipe types. */
public class RecipeTypes
{
    public static final mezz.jei.api.recipe.RecipeType<Object> CRAFTING =
        new mezz.jei.api.recipe.RecipeType<>(new ResourceLocation("minecraft", "crafting"), Object.class);
    public static final mezz.jei.api.recipe.RecipeType<Object> SMELTING =
        new mezz.jei.api.recipe.RecipeType<>(new ResourceLocation("minecraft", "smelting"), Object.class);
}

