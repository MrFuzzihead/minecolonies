package net.minecraftforge.common.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.Nullable;
import java.util.concurrent.CompletableFuture;

/** [1.7.10 bridge] BiomeTagsProvider - no 1.7.10 equivalent (data generation) */
public abstract class BiomeTagsProvider implements net.minecraft.data.DataProvider
{
    public BiomeTagsProvider(final PackOutput output, final CompletableFuture<?> lookupProvider, final String modId, @Nullable final ExistingFileHelper existingFileHelper) {}

    protected abstract void addTags(HolderLookup.Provider holder);

    @Override
    public String getName() { return "BiomeTagsProvider"; }

    @Override
    public CompletableFuture<?> run(final net.minecraft.data.CachedOutput cache) { return CompletableFuture.completedFuture(null); }
}
