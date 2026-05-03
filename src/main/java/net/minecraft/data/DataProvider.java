package net.minecraft.data;

import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;
import java.nio.file.Path;
import com.google.gson.JsonElement;

/**
 * [1.7.10] Stub for 1.21 DataProvider interface.
 * Data generation is not used in 1.7.10; research JSONs are bundled in resources.
 */
public interface DataProvider
{
    /** No-op equivalent of DataProvider.saveStable in 1.21. */
    static CompletableFuture<?> saveStable(final CachedOutput cache, final JsonElement data, final Path path)
    {
        // TODO: no 1.7.10 data generation equivalent
        return CompletableFuture.completedFuture(null);
    }

    @NotNull
    String getName();

    CompletableFuture<?> run(@NotNull CachedOutput cache);
}

