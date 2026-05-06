package net.minecraft.data.recipes;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.crafting.FinishedRecipe;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
/** [1.7.10 bridge] RecipeProvider */
public abstract class RecipeProvider implements DataProvider {
    protected final PackOutput output;
    protected RecipeProvider(final PackOutput output) { this.output = output; }
    protected abstract void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer);
    @Override public String getName() { return "RecipeProvider"; }
    @Override public CompletableFuture<?> run(@NotNull CachedOutput cache) { return CompletableFuture.completedFuture(null); }
}
