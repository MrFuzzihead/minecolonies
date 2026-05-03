package net.minecraft.server.packs.resources;

/**
 * [1.7.10] Compatibility shim for 1.21 SimplePreparableReloadListener.
 * In 1.7.10, resource reload listeners use different events.
 *
 * @param <T> the prepared data type.
 */
public abstract class SimplePreparableReloadListener<T>
{
    protected abstract T prepare(ResourceManager resourceManager, Object profiler);
    protected abstract void apply(T object, ResourceManager resourceManager, Object profiler);

    public void onResourceManagerReload(final ResourceManager resourceManager)
    {
        // no-op shim
    }
}

