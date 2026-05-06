package net.minecraft.server.packs.resources;

/** [1.7.10 bridge] PreparableReloadListener - base interface for resource reload listeners */
public interface PreparableReloadListener
{
    default void onResourceManagerReload(final ResourceManager resourceManager) {}
}

