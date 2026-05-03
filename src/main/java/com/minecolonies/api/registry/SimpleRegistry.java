package com.minecolonies.api.registry;

// [1.7.10 BACKPORT] Replacement for net.minecraftforge.registries.IForgeRegistry<T>.
//
// In 1.21, MineColonies used Forge's IForgeRegistry<T> system (NewRegistryEvent / RegistryBuilder)
// to store mod-internal types such as BuildingEntry, JobEntry, GuardType, etc.
//
// In 1.7.10 there is no IForgeRegistry concept for custom types. This class provides
// a minimal drop-in replacement backed by a LinkedHashMap.
//
// Callers that previously received IForgeRegistry<T> now receive SimpleRegistry<T>.

import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A minimal ordered registry mapping {@link ResourceLocation} keys to values.
 * <p>
 * Replaces {@code net.minecraftforge.registries.IForgeRegistry<T>} throughout the
 * MineColonies 1.7.10 backport.
 * </p>
 *
 * @param <T> the registry value type.
 */
public class SimpleRegistry<T> implements Iterable<T>
{
    private final LinkedHashMap<ResourceLocation, T> entries = new LinkedHashMap<>();

    /** Default key used when no key is provided (mirrors RegistryBuilder.setDefaultKey). */
    @Nullable
    private ResourceLocation defaultKey;

    public SimpleRegistry() {}

    public SimpleRegistry(@Nullable final ResourceLocation defaultKey)
    {
        this.defaultKey = defaultKey;
    }

    /**
     * Register an entry under the given key.
     *
     * @param key   the registry key.
     * @param value the value.
     */
    public void register(@NotNull final ResourceLocation key, @NotNull final T value)
    {
        entries.put(key, value);
    }

    /**
     * Get a value by key, or {@code null} if not found.
     *
     * @param key the key to look up.
     * @return the value, or {@code null}.
     */
    @Nullable
    public T getValue(@NotNull final ResourceLocation key)
    {
        return entries.get(key);
    }

    /**
     * Check if the registry contains the given key.
     *
     * @param key the key to test.
     * @return true if present.
     */
    public boolean containsKey(@NotNull final ResourceLocation key)
    {
        return entries.containsKey(key);
    }

    /**
     * Return all registered values, in insertion order.
     *
     * @return an unmodifiable collection of values.
     */
    @NotNull
    public Collection<T> getValues()
    {
        return Collections.unmodifiableCollection(entries.values());
    }

    /**
     * Return all registered keys.
     *
     * @return an unmodifiable set of keys.
     */
    @NotNull
    public Set<ResourceLocation> getKeys()
    {
        return Collections.unmodifiableSet(entries.keySet());
    }

    /** The number of registered entries. */
    public int size()
    {
        return entries.size();
    }

    @Override
    @NotNull
    public Iterator<T> iterator()
    {
        return entries.values().iterator();
    }
}

