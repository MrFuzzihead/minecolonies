package net.minecraftforge.registries;

import java.util.function.Supplier;

/**
 * [1.7.10] Compatibility shim for Forge 1.16+ RegistryObject.
 * Wraps a lazily-supplied registry value.
 *
 * @param <T> the type of the registry entry.
 */
public class RegistryObject<T>
{
    private final Supplier<T> supplier;

    private RegistryObject(final Supplier<T> supplier)
    {
        this.supplier = supplier;
    }

    public static <T> RegistryObject<T> of(final Supplier<T> supplier)
    {
        return new RegistryObject<>(supplier);
    }

    public T get()
    {
        return supplier.get();
    }
}

