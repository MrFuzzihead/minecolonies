package net.minecraft.resources;

/**
 * [1.7.10] Compatibility stub for 1.21 ResourceKey<T>.
 */
public class ResourceKey<T>
{
    private final net.minecraft.util.ResourceLocation location;

    private ResourceKey(net.minecraft.util.ResourceLocation location)
    {
        this.location = location;
    }

    public static <T> ResourceKey<T> of(ResourceKey<?> registry, net.minecraft.util.ResourceLocation location)
    {
        return new ResourceKey<>(location);
    }

    public net.minecraft.util.ResourceLocation location() { return location; }
    public String toString() { return location.toString(); }
}

