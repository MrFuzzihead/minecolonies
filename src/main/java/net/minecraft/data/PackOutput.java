package net.minecraft.data;

import net.minecraft.util.ResourceLocation;
import java.nio.file.Path;

/**
 * [1.7.10] Stub for 1.21 PackOutput.
 * No data generation in 1.7.10.
 */
public class PackOutput
{
    public enum Target { RESOURCE_PACK, DATA_PACK }

    public PathProvider createPathProvider(final Target target, final String subDirectory)
    {
        return new PathProvider(target, subDirectory);
    }

    public static class PathProvider
    {
        private final Target target;
        private final String subDirectory;

        public PathProvider(final Target target, final String subDirectory)
        {
            this.target = target;
            this.subDirectory = subDirectory;
        }

        public Path json(final ResourceLocation id)
        {
            // TODO: no 1.7.10 equivalent
            return Path.of(subDirectory, id.getNamespace(), id.getPath() + ".json");
        }
    }
}

