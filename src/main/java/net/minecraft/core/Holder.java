package net.minecraft.core;

import java.util.Optional;
import net.minecraft.resources.ResourceKey;

/**
 * [1.7.10] Compatibility stub for 1.21 Holder<T>.
 */
public class Holder<T>
{
    private final T value;

    public Holder(T value) { this.value = value; }

    public T value() { return value; }
    public boolean isBound() { return value != null; }
    public Optional<ResourceKey<T>> unwrapKey() { return Optional.empty(); }

    public static <T> Holder<T> direct(T value) { return new Holder<>(value); }
}

