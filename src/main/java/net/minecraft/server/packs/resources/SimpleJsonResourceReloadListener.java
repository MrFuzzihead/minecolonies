package net.minecraft.server.packs.resources;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * [1.7.10] Stub for 1.21 SimpleJsonResourceReloadListener.
 * In 1.7.10, JSON resources are loaded from classpath at startup rather than through a reload listener system.
 * Subclasses should call {@link #loadFromClasspath()} during FMLPostInitializationEvent.
 */
public abstract class SimpleJsonResourceReloadListener
{
    private final Gson   gson;
    private final String directory;

    public SimpleJsonResourceReloadListener(final Gson gson, final String directory)
    {
        this.gson = gson;
        this.directory = directory;
    }

    /**
     * Called with the loaded JSON map.
     * In 1.7.10 this is triggered manually from loadFromClasspath().
     */
    protected abstract void apply(
        @NotNull Map<ResourceLocation, JsonElement> object,
        @NotNull ResourceManager resourceManager,
        @NotNull ProfilerFiller profiler);

    /**
     * [1.7.10] Load JSON files from the classpath under the given directory.
     * Replaces the 1.21 reload-listener mechanism.
     */
    public void loadFromClasspath()
    {
        // TODO: implement classpath JSON scanning for 1.7.10
        // For now: no-op placeholder — subclass logic runs when explicitly called
    }
}


