package net.minecraft.core;
import java.util.Optional;
/** [1.7.10 bridge] Holder */
public interface Holder<T> {
    T value();
    Optional<net.minecraft.resources.ResourceKey<T>> unwrapKey();
}