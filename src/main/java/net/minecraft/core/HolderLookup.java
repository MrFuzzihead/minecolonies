package net.minecraft.core;
/** [1.7.10] Stub for 1.21 HolderLookup */
public interface HolderLookup {
    /** Inner class Provider */
    interface Provider {
        <T> java.util.Optional<HolderLookup.RegistryLookup<T>> lookup(net.minecraft.resources.ResourceKey<net.minecraft.core.Registry<T>> key);
    }
    interface RegistryLookup<T> {}
}