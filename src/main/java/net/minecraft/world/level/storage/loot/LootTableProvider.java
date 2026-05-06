package net.minecraft.world.level.storage.loot;

import net.minecraft.util.ResourceLocation;
import net.minecraft.data.PackOutput;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/** [1.7.10 bridge] LootTableProvider - no 1.7.10 equivalent */
public class LootTableProvider implements DataProvider
{
    public interface SubProviderEntry
    {
    }

    public LootTableProvider(final PackOutput output, final Set<ResourceLocation> requiredTables, final List<?> subProviders) {}

    public List<SubProviderEntry> getTables() { return java.util.Collections.emptyList(); }

    protected void validate(@NotNull Map<ResourceLocation, LootTable> map, @NotNull ValidationContext tracker) {}

    @NotNull
    @Override
    public String getName() { return "LootTableProvider"; }

    @Override
    public CompletableFuture<?> run(@NotNull final CachedOutput cache) { return CompletableFuture.completedFuture(null); }
}

