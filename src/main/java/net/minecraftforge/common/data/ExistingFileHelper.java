package net.minecraftforge.common.data;

import net.minecraft.util.ResourceLocation;

/** [1.7.10 bridge] ExistingFileHelper - no 1.7.10 equivalent (data generation) */
public class ExistingFileHelper
{
    public Object getResource(
        final ResourceLocation location,
        final Object type,
        final String suffix,
        final String pathPrefix)
    {
        throw new UnsupportedOperationException("No data generation in 1.7.10");
    }

    public boolean exists(
        final ResourceLocation location,
        final Object type,
        final String suffix,
        final String pathPrefix)
    {
        return false;
    }
}
