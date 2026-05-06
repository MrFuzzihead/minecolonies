package net.minecraftforge.client.model.generators;
import net.minecraft.util.ResourceLocation;
/** [1.7.10 bridge] ModelFile */
public abstract class ModelFile {
    public static class ExistingModelFile extends ModelFile {
        public ExistingModelFile(ResourceLocation loc) {}
    }
    public static class UncheckedModelFile extends ModelFile {
        public UncheckedModelFile(String path) {}
        public UncheckedModelFile(ResourceLocation loc) {}
    }
}
