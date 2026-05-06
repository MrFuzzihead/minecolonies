package net.minecraftforge.common.data;

/** [1.7.10 bridge] ExistingFileHelper - no 1.7.10 equivalent (data generation) */
public class ExistingFileHelper
{
    public net.minecraft.server.packs.resources.Resource getResource(
        final net.minecraft.util.ResourceLocation location,
        final net.minecraft.server.packs.PackType type,
        final String suffix,
        final String pathPrefix)
    {
        throw new UnsupportedOperationException("No data generation in 1.7.10");
    }

    public boolean exists(
        final net.minecraft.util.ResourceLocation location,
        final net.minecraft.server.packs.PackType type,
        final String suffix,
        final String pathPrefix)
    {
        return false;
    }
}

