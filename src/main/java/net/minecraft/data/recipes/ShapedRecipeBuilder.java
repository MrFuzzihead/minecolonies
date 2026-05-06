package net.minecraft.data.recipes;
import net.minecraft.world.item.crafting.FinishedRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import java.util.function.Consumer;
/** [1.7.10 bridge] ShapedRecipeBuilder */
public class ShapedRecipeBuilder {
    public static ShapedRecipeBuilder shaped(RecipeCategory cat, ItemLike result) { return new ShapedRecipeBuilder(); }
    public static ShapedRecipeBuilder shaped(RecipeCategory cat, ItemLike result, int count) { return new ShapedRecipeBuilder(); }
    public static ShapedRecipeBuilder shaped(RecipeCategory cat, Object result) { return new ShapedRecipeBuilder(); }
    public ShapedRecipeBuilder pattern(String p) { return this; }
    public ShapedRecipeBuilder define(char c, ItemLike item) { return this; }
    public ShapedRecipeBuilder define(char c, TagKey<Item> tag) { return this; }
    public ShapedRecipeBuilder define(char c, Object o) { return this; }
    public ShapedRecipeBuilder unlockedBy(String name, Object trigger) { return this; }
    public void save(Consumer<FinishedRecipe> consumer) {}
    public void save(Consumer<FinishedRecipe> consumer, net.minecraft.util.ResourceLocation id) {}
}
