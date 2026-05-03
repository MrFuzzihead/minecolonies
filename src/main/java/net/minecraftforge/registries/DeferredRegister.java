package net.minecraftforge.registries;

import net.minecraft.util.ResourceLocation;

import java.util.function.Supplier;

/**
 * [1.7.10] Compatibility shim for Forge 1.16+ DeferredRegister.
 * Provides a no-op registry wrapper for compile compatibility.
 *
 * @param <T> the type of entries in this registry.
 */
public class DeferredRegister<T>
{
    private DeferredRegister() {}

    public static <T> DeferredRegister<T> create(final ResourceLocation registryKey, final String modId)
    {
        return new DeferredRegister<>();
    }

    public static <T> DeferredRegister<T> create(final String registryKey, final String modId)
    {
        return new DeferredRegister<>();
    }

    public <I extends T> RegistryObject<I> register(final String name, final Supplier<? extends I> supplier)
    {
        return RegistryObject.of(() -> supplier.get());
    }
}

