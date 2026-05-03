package com.minecolonies.api.registry;

// [1.7.10 BACKPORT] Replacement for net.minecraftforge.registries.RegistryObject<T>.
//
// In 1.21, DeferredRegister.register() returns a RegistryObject<T> that lazily provides
// the registered value via get(). In 1.7.10, there is no deferred registration system;
// this shim holds the value directly.

import java.util.function.Supplier;

/**
 * A simple wrapper that mimics the RegistryObject.get() pattern from 1.21 Forge.
 * In 1.7.10 the value is set directly (eagerly).
 *
 * @param <T> the type of the registry entry.
 */
public class RegistryObject<T> implements Supplier<T>
{
    private T value;

    private RegistryObject(final T value)
    {
        this.value = value;
    }

    /**
     * Create a RegistryObject holding the given value.
     *
     * @param value the value.
     * @param <T>   the type.
     * @return the RegistryObject.
     */
    public static <T> RegistryObject<T> of(final T value)
    {
        return new RegistryObject<>(value);
    }

    /**
     * Get the held value.
     *
     * @return the value.
     */
    @Override
    public T get()
    {
        return value;
    }

    /**
     * Update the held value (used during registry initialization).
     *
     * @param value the new value.
     */
    public void set(final T value)
    {
        this.value = value;
    }

    /**
     * @return true if the value is non-null.
     */
    public boolean isPresent()
    {
        return value != null;
    }
}

