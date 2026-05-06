package net.minecraftforge.client.event;
import net.minecraft.world.item.crafting.RecipeType;
import java.util.function.Function;
/** [1.7.10 bridge] RegisterRecipeBookCategoriesEvent */
public class RegisterRecipeBookCategoriesEvent {
    public void registerRecipeCategoryFinder(RecipeType<?> type, Function<?, ?> finder) {}
}
