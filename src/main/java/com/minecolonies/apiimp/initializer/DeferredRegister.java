package com.minecolonies.apiimp.initializer;

import net.minecraft.util.ResourceLocation;

import java.util.function.Supplier;

/**
 * [1.7.10 BACKPORT] Minimal shim for net.minecraftforge.registries.DeferredRegister.
 * In 1.7.10, there is no deferred registry system. Registration is immediate.
 * register() calls the supplier and returns the value directly.
 *
 * @param <T> the registry entry type
 */
public final class DeferredRegister<T>
{
    private DeferredRegister() {}

    /**
     * Create a new DeferredRegister (no-op shim).
     *
     * @param registryKey the registry key (unused in 1.7.10)
     * @param modId       the mod id (unused)
     * @param <T>         entry type
     * @return a new DeferredRegister instance
     */
    public static <T> DeferredRegister<T> create(final Object registryKey, final String modId)
    {
        return new DeferredRegister<>();
    }

    /**
     * Register an entry by calling the supplier immediately and returning the value.
     *
     * @param path     the entry path (unused in 1.7.10)
     * @param supplier the factory supplier
     * @return the created entry value
     */
    public T register(final String path, final Supplier<T> supplier)
    {
        return supplier.get();
    }

    /**
     * Register an entry by ResourceLocation key.
     */
    public T register(final ResourceLocation key, final Supplier<T> supplier)
    {
        return supplier.get();
    }
}

