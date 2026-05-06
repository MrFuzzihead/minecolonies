package net.minecraftforge.common.data;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Nullable;
import java.util.concurrent.CompletableFuture;
/** [1.7.10 bridge] TagsProvider */
public abstract class TagsProvider<T> implements DataProvider {
    protected TagsProvider(PackOutput output, Object registryKey, CompletableFuture<?> provider, String modId, @Nullable ExistingFileHelper helper) {}
    protected TagsProvider(PackOutput output, Object registryKey, CompletableFuture<?> provider, @Nullable ExistingFileHelper helper) {}
    protected abstract void addTags(HolderLookup.Provider lookup);
    protected TagAppender<T> NBTBase(TagKey<T> tag) { return new TagAppender<>(); }
    protected TagAppender<T> tag(TagKey<T> tag) { return new TagAppender<>(); }
    @Override public String getName() { return "TagsProvider"; }
    @Override public CompletableFuture<?> run(CachedOutput cache) { return CompletableFuture.completedFuture(null); }
    public static class TagAppender<T> {
        @SafeVarargs public final TagAppender<T> add(T... values) { return this; }
        public TagAppender<T> addTag(TagKey<T> tag) { return this; }
    }
}
