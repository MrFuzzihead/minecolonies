package net.minecraftforge.registries;

import net.minecraft.util.ResourceLocation;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Compatibility shim: replaces the Forge 1.12+ IForgeRegistry for the 1.7.10 backport.
 * Provides a simple registry backed by a HashMap.
 *
 * @param <V> the type of entries stored in this registry.
 */
public class IForgeRegistry<V>
{
    private final Map<ResourceLocation, V> entries = new HashMap<>();

    public void register(final ResourceLocation key, final V value)
    {
        entries.put(key, value);
    }

    public V getValue(final ResourceLocation key)
    {
        return entries.get(key);
    }

    public boolean containsKey(final ResourceLocation key)
    {
        return entries.containsKey(key);
    }

    public Collection<V> getValues()
    {
        return entries.values();
    }

    public Collection<ResourceLocation> getKeys()
    {
        return entries.keySet();
    }
}

