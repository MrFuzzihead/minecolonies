package net.minecraftforge.common.data;

import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.Nullable;
import java.util.concurrent.CompletableFuture;

/** [1.7.10 bridge] BlockTagsProvider - no 1.7.10 equivalent (data generation) */
public abstract class BlockTagsProvider implements net.minecraft.data.DataProvider
{
    public BlockTagsProvider(final PackOutput output, final CompletableFuture<?> lookupProvider, final ExistingFileHelper existingFileHelper) {}
    public BlockTagsProvider(final PackOutput output, final CompletableFuture<?> lookupProvider, final String modId, @Nullable final ExistingFileHelper existingFileHelper) {}

    @Override
    public String getName() { return "BlockTagsProvider"; }

    @Override
    public CompletableFuture<?> run(final net.minecraft.data.CachedOutput cache) { return CompletableFuture.completedFuture(null); }
}

