package mezz.jei.api;

import net.minecraft.util.ResourceLocation;

/** [1.7.10 stub] IModPlugin. */
public interface IModPlugin
{
    ResourceLocation getPluginUid();
    default void registerCategories(mezz.jei.api.registration.IRecipeCategoryRegistration registration) {}
    default void registerRecipes(mezz.jei.api.registration.IRecipeRegistration registration) {}
    default void registerRecipeTransferHandlers(mezz.jei.api.registration.IRecipeTransferRegistration registration) {}
    default void onRuntimeAvailable(mezz.jei.api.runtime.IJeiRuntime jeiRuntime) {}
}

