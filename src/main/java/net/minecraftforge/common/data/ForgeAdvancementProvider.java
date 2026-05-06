package net.minecraftforge.common.data;

import net.minecraft.data.PackOutput;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/** [1.7.10 bridge] ForgeAdvancementProvider - no 1.7.10 equivalent (data generation) */
public abstract class ForgeAdvancementProvider implements net.minecraft.data.DataProvider
{
    @FunctionalInterface
    public interface AdvancementGenerator
    {
        void generate(Object registries, Consumer<?> consumer, ExistingFileHelper fileHelper);
    }

    public ForgeAdvancementProvider(final PackOutput output, final CompletableFuture<?> lookupProvider, final ExistingFileHelper existingFileHelper, final List<AdvancementGenerator> generators) {}

    @Override
    public String getName() { return "ForgeAdvancementProvider"; }

    @Override
    public CompletableFuture<?> run(final net.minecraft.data.CachedOutput cache) { return CompletableFuture.completedFuture(null); }
}

